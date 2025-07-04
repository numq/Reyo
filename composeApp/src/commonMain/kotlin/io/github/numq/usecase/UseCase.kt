package io.github.numq.usecase

interface UseCase<in Input, out Output> {
    suspend fun execute(input: Input): Result<Output>
}