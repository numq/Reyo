package io.github.numq.reyo.media

sealed interface Media {
    data class Audio(val bytes: ByteArray, val sampleRate: Int, val channels: Int) : Media {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Audio

            if (sampleRate != other.sampleRate) return false
            if (channels != other.channels) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = sampleRate
            result = 31 * result + channels
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }
}