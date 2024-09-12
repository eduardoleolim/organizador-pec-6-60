package org.eduardoleolim.organizadorpec660.instrument.domain

import java.util.*

class InstrumentFile private constructor(private val id: InstrumentFileId, private var content: InstrumentContent) {
    companion object {
        fun create(content: ByteArray) = InstrumentFile(InstrumentFileId.random(), InstrumentContent(content))

        fun from(id: String, content: ByteArray) =
            InstrumentFile(InstrumentFileId.fromString(id), InstrumentContent(content))
    }

    fun id() = id.value

    fun content() = content.value

    fun changeContent(content: ByteArray) {
        this.content = InstrumentContent(content)
    }
}

data class InstrumentFileId(val value: UUID) {
    companion object {
        fun random() = InstrumentFileId(UUID.randomUUID())

        fun fromString(value: String) = try {
            InstrumentFileId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidInstrumentFileIdError(value, e)
        }
    }
}

data class InstrumentContent(val value: ByteArray) {
    init {
        validate(value)
    }

    private fun validate(value: ByteArray) {
        if (value.isEmpty())
            throw InvalidEmptyInstrumentContentError()

        val pdfHeader = byteArrayOf(0x25, 0x50, 0x44, 0x46)
        val valueHeader = value.copyOfRange(0, pdfHeader.size)

        if (valueHeader.contentEquals(pdfHeader).not())
            throw InvalidInstrumentContentError()
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null || javaClass != other.javaClass -> false
            else -> {
                other as InstrumentContent
                value.contentEquals(other.value)
            }
        }
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}
