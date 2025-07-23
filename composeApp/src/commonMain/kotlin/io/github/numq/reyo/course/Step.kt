package io.github.numq.reyo.course

sealed interface Step {
    val id: String

    val content: String

    data class Theory(override val id: String, override val content: String) : Step

    sealed class Practice private constructor(override val id: String, override val content: String) : Step {
        data class Listening(override val id: String, override val content: String) : Practice(id, content)

        data class Reading(override val id: String, override val content: String) : Practice(id, content)

        data class Writing(override val id: String, override val content: String) : Practice(id, content)
    }
}