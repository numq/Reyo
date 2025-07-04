package io.github.numq.recognition

data class InputImage(val bytes: ByteArray, val width: Int, val height: Int, val channels: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InputImage

        if (width != other.width) return false
        if (height != other.height) return false
        if (channels != other.channels) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + channels
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}