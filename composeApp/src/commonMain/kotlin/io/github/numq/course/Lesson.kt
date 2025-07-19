package io.github.numq.course

sealed interface Lesson {
    val id: String

    data class Theory(override val id: String, val content: String) : Lesson

    data class Practice(override val id: String, val task: String, val answer: String) : Lesson
}