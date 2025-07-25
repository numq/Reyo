package io.github.numq.reyo.recognition

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import kotlinx.serialization.json.Json
import nu.pattern.OpenCV
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.io.File
import java.nio.FloatBuffer

class KerasRecognitionService(modelPath: String, labelsPath: String) : RecognitionService {
    private companion object {
        init {
            OpenCV.loadLocally()
        }

        const val WIDTH = 32

        const val HEIGHT = 32
    }

    private val labels = Json.decodeFromString<Map<String, String>>(File(labelsPath).readText())

    private val environment = OrtEnvironment.getEnvironment()

    private val session = environment.createSession(modelPath)

    private fun preprocessImage(pixels: IntArray, width: Int, height: Int): FloatArray {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        image.setRGB(0, 0, width, height, pixels, 0, width)

        val mat = Mat(height, width, CvType.CV_8UC4)

        val data = (image.raster.dataBuffer as java.awt.image.DataBufferInt).data

        val byteBuffer = ByteArray(width * height * 4)
        for (i in data.indices) {
            val argb = data[i]
            byteBuffer[i * 4 + 0] = (argb shr 16 and 0xFF).toByte()
            byteBuffer[i * 4 + 1] = (argb shr 8 and 0xFF).toByte()
            byteBuffer[i * 4 + 2] = (argb and 0xFF).toByte()
            byteBuffer[i * 4 + 3] = (argb shr 24 and 0xFF).toByte()
        }

        mat.put(0, 0, byteBuffer)

        val bgrMat = Mat()

        Imgproc.cvtColor(mat, bgrMat, Imgproc.COLOR_RGBA2BGR)

        val resizedMat = Mat()

        Imgproc.resize(bgrMat, resizedMat, Size(WIDTH.toDouble(), HEIGHT.toDouble()))

        resizedMat.convertTo(resizedMat, CvType.CV_32FC3, 1.0 / 255.0)

        val floatArray = FloatArray(WIDTH * HEIGHT * 3)

        resizedMat.get(0, 0, floatArray)

        return floatArray
    }

    override suspend fun recognize(pixels: IntArray, width: Int, height: Int) = runCatching {
        val processedImage = preprocessImage(pixels, width, height)

        OnnxTensor.createTensor(
            environment,
            FloatBuffer.wrap(processedImage),
            longArrayOf(1, WIDTH.toLong(), HEIGHT.toLong(), 3)
        ).use { inputTensor ->
            val results = session.run(mapOf(session.inputNames.first() to inputTensor))

            val output = results[0].value as Array<FloatArray>

            val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1

            labels[predictedIndex.toString()]
        }
    }

    override suspend fun close() = runCatching {
        session.close()

        environment.close()
    }
}