package io.github.numq.reyo.course

sealed interface Lesson {
    val id: String

    val steps: List<Step>
}