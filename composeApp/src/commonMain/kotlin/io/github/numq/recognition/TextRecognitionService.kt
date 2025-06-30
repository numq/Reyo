package io.github.numq.recognition

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bytedeco.tesseract.TessBaseAPI
import org.jetbrains.compose.resources.InternalResourceApi
import java.io.File

interface TextRecognitionService {
    suspend fun recognize(
        bytes: ByteArray, width: Int, height: Int, bytesPerPixel: Int, bytesPerLine: Int
    ): Result<String>

    suspend fun close(): Result<Unit>

    @OptIn(InternalResourceApi::class)
    class Implementation : TextRecognitionService {
        private val mutex = Mutex()

        private val tess by lazy {
            TessBaseAPI().apply {
                val trainedData = checkNotNull(
                    File("src\\commonMain\\composeResources\\files")
                ) { "Trained data is missing" }.path

                check(Init(trainedData, "Hangul") >= 0) { "Could not initialize Tesseract" }
            }
        }

        override suspend fun recognize(
            bytes: ByteArray, width: Int, height: Int, bytesPerPixel: Int, bytesPerLine: Int
        ) = mutex.withLock {
            runCatching {
                tess.SetImage(bytes, width, height, bytesPerPixel, bytesPerLine)

                tess.GetUTF8Text()?.string ?: ""
            }
        }

        override suspend fun close() = mutex.withLock {
            runCatching {
                tess.close()
            }
        }
    }
}