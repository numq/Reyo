package io.github.numq

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform