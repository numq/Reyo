package io.github.numq.reyo.recognition

interface RecognitionService {
    suspend fun recognize(pixels: IntArray, width: Int, height: Int): Result<String?>

    suspend fun close(): Result<Unit>
}