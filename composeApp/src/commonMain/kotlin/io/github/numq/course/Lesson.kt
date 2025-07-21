package io.github.numq.course

sealed interface Lesson {
    val id: String

    val steps: List<Step>
}