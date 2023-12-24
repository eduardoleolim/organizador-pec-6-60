package org.eduardoleolim.organizadorpec660.core.instrument.domain

import java.nio.Buffer
import java.util.*

class InstrumentFile private constructor(private val id: InstrumentFileId, private val content: InstrumentContent) {
    companion object {
        fun create(content: Buffer) = InstrumentFile(InstrumentFileId.random(), InstrumentContent(content))
    }

    fun id() = id.value

    fun content() = content.value
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

data class InstrumentContent(val value: Buffer)
