package org.eduardoleolim.organizadorPec660.core.instrumentType.domain

import java.util.*

class InstrumentType private constructor(
    private val id: InstrumentTypeId,
    private var name: InstrumentTypeName,
    private val createdAt: InstrumentTypeCreateDate,
    private var updatedAt: InstrumentTypeUpdateDate?
) {
    companion object {
        fun create(name: String) = InstrumentType(
            InstrumentTypeId.random(),
            InstrumentTypeName(name),
            InstrumentTypeCreateDate.now(),
            null
        )

        fun from(
            id: String,
            name: String,
            createdAt: Date,
            updatedAt: Date?
        ) = InstrumentType(
            InstrumentTypeId.fromString(id),
            InstrumentTypeName(name),
            InstrumentTypeCreateDate(createdAt),
            updatedAt?.let {
                if (it.before(createdAt))
                    throw InvalidInstrumentTypeUpdateDateError(it, createdAt)

                InstrumentTypeUpdateDate(it)
            }
        )
    }

    fun id() = id.value

    fun name() = name.value

    fun createdAt() = createdAt.value

    fun updatedAt() = updatedAt?.value

    fun changeName(name: String) {
        this.name = InstrumentTypeName(name)
        this.updatedAt = InstrumentTypeUpdateDate.now()
    }

}

data class InstrumentTypeId(val value: UUID) {
    companion object {
        fun random() = InstrumentTypeId(UUID.randomUUID())

        fun fromString(value: String) = try {
            InstrumentTypeId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidInstrumentTypeIdError(value, e)
        }
    }
}

data class InstrumentTypeName(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (value.isBlank()) {
            throw InvalidInstrumentTypeNameError(value)
        }
    }
}

data class InstrumentTypeCreateDate(val value: Date) {
    companion object {
        fun now() = InstrumentTypeCreateDate(Date())
    }
}

data class InstrumentTypeUpdateDate(val value: Date) {
    companion object {
        fun now() = InstrumentTypeUpdateDate(Date())
    }
}
