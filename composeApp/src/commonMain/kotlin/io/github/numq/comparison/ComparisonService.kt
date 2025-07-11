package io.github.numq.comparison

interface ComparisonService {
    suspend fun recognize(pixels: IntArray, width: Int, height: Int): Result<String>

    suspend fun compare(src: IntArray, dst: IntArray, width: Int, height: Int): Result<Boolean>

    suspend fun close(): Result<Unit>
}