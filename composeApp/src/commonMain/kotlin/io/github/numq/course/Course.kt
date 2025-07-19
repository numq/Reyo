package io.github.numq.course

data class Course(
    val id: String,
    val name: String,
    val description: String,
    val lessons: List<Lesson>
)