import os

import numpy as np
import tensorflow as tf
import tf2onnx
from sklearn.model_selection import train_test_split
from tensorflow.keras import layers, models, callbacks

INPUT_SIZE = 64
BATCH_SIZE = 32
EPOCHS = 50
PATIENCE = 5


def load_data(data_dir):
    try:
        with open(os.path.join(data_dir, "labels.txt"), "r", encoding="utf-8") as f:
            characters = [line.strip() for line in f if line.strip()]

        images = []

        labels = []

        with open(os.path.join(data_dir, "mapping.txt"), "r", encoding="utf-8") as f:
            for line in f:
                if not line.strip():
                    continue

                filename, label_idx = line.strip().split()
                img_path = os.path.join(data_dir, filename)

                if not os.path.exists(img_path):
                    raise FileNotFoundError(f"Image file missing: {img_path}")

                img = cv2.imread(img_path, cv2.IMREAD_GRAYSCALE)
                if img is None:
                    raise ValueError(f"Failed to read image: {img_path}")

                images.append(img.astype(np.float32) / 255.0)
                labels.append(int(label_idx))

        return np.array(images), np.array(labels), characters

    except Exception as e:
        print(f"Error loading data: {str(e)}")
        raise


def build_model(input_shape, num_classes):
    """Enhanced CNN model with dropout"""
    model = models.Sequential([
        layers.Reshape((*input_shape, 1), input_shape=input_shape),
        layers.Conv2D(32, (3, 3), activation='relu', padding='same'),
        layers.BatchNormalization(),
        layers.MaxPooling2D((2, 2)),
        layers.Dropout(0.25),

        layers.Conv2D(64, (3, 3), activation='relu', padding='same'),
        layers.BatchNormalization(),
        layers.MaxPooling2D((2, 2)),
        layers.Dropout(0.25),

        layers.Flatten(),
        layers.Dense(128, activation='relu'),
        layers.BatchNormalization(),
        layers.Dropout(0.5),
        layers.Dense(num_classes, activation='softmax')
    ])

    model.summary()
    return model


def train():
    X, y, characters = load_data("data")
    print(f"Loaded {len(X)} images for {len(characters)} characters")

    X = X.reshape(-1, INPUT_SIZE, INPUT_SIZE)
    X_train, X_val, y_train, y_val = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y)

    model = build_model((INPUT_SIZE, INPUT_SIZE), len(characters))

    optimizer = tf.keras.optimizers.Adam(learning_rate=0.001)
    model.compile(
        optimizer=optimizer,
        loss='sparse_categorical_crossentropy',
        metrics=['accuracy',
                 tf.keras.metrics.SparseTopKCategoricalAccuracy(k=3, name='top3_accuracy')]
    )

    callbacks_list = [
        callbacks.EarlyStopping(patience=PATIENCE, restore_best_weights=True),
        callbacks.ModelCheckpoint(
            "best_model.h5",
            save_best_only=True,
            monitor='val_accuracy',
            mode='max'
        ),
        callbacks.ReduceLROnPlateau(
            monitor='val_loss',
            factor=0.5,
            patience=2,
            min_lr=1e-6
        )
    ]

    history = model.fit(
        X_train, y_train,
        validation_data=(X_val, y_val),
        epochs=EPOCHS,
        batch_size=BATCH_SIZE,
        callbacks=callbacks_list,
        verbose=1
    )

    return model, characters


def convert_to_onnx(model, output_path="model.onnx"):
    input_signature = [tf.TensorSpec([None, INPUT_SIZE, INPUT_SIZE, 1], tf.float32, name='input')]
    model_proto, _ = tf2onnx.convert.from_keras(
        model,
        input_signature=input_signature,
        output_path=output_path
    )
    print(f"Model saved as {output_path}")


if __name__ == "__main__":
    trained_model, class_names = train()

    with open("class_names.txt", "w", encoding="utf-8") as f:
        f.write("\n".join(class_names))

    convert_to_onnx(trained_model)

    print("Training completed successfully")
