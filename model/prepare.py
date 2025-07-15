import datetime
import json
import os
import shutil

import albumentations as A
import cv2
import numpy as np
from PIL import Image, ImageDraw, ImageFont

from letters import hangul_letters

output_dir = "data"
images_dir = "images"
os.makedirs(output_dir, exist_ok=True)

font_path = "fonts"
font_name = "NotoSansKR-Regular.otf"
font_size = 48
image_size = 64
augmentations = 50

font = ImageFont.truetype(os.path.join(font_path, font_name), font_size)

transform = A.Compose([
    A.Blur(),
    A.Downscale(),
    A.GaussNoise(),
    A.OpticalDistortion()
])

if os.path.exists(output_dir):
    shutil.rmtree(output_dir)

os.makedirs(os.path.join(output_dir, images_dir), exist_ok=True)

with open(os.path.join(output_dir, "labels.txt"), "w", encoding="utf-8") as labels:
    for char in hangul_letters:
        labels.write(f"{char}\n")

with open(os.path.join(output_dir, "mapping.txt"), "w", encoding="utf-8") as mapping:
    count = 0

    for idx, char in enumerate(hangul_letters):
        img = Image.new("L", (image_size, image_size), 255)

        draw = ImageDraw.Draw(img)

        bbox = draw.textbbox((0, 0), char, font=font)

        w = bbox[2] - bbox[0]

        h = bbox[3] - bbox[1]

        x = (image_size - w) // 2 - bbox[0]

        y = (image_size - h) // 2 - bbox[1]

        draw.text((x, y), char, font=font, fill=0)

        base_np = np.array(img)

        filename = f"{count:04d}.png"

        cv2.imwrite(os.path.join(output_dir, images_dir, filename), base_np)

        mapping.write(f"{filename} {idx}\n")

        count += 1

        for i in range(augmentations):
            augmented = transform(image=base_np)["image"]

            filename = f"{count:04d}.png"

            cv2.imwrite(os.path.join(output_dir, images_dir, filename), augmented)

            mapping.write(f"{filename} {idx}\n")

            count += 1

with open(os.path.join(output_dir, "meta.json"), "w", encoding="utf-8") as f:
    json.dump({
        "font_name": font_name,
        "font_size": font_size,
        "image_size": image_size,
        "augmentations": augmentations,
        "created_at": datetime.datetime.now().isoformat()
    }, f)
