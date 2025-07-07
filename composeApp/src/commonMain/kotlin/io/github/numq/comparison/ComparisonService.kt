package io.github.numq.comparison

interface ComparisonService {
    suspend fun compare(src: IntArray, dst: IntArray, width: Int, height: Int): Result<Boolean>

    suspend fun close(): Result<Unit>
}