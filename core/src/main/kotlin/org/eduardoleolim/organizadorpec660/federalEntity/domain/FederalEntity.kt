package org.eduardoleolim.organizadorpec660.federalEntity.domain

import java.util.*

class FederalEntity private constructor(
    private val id: FederalEntityId,
    private var keyCode: FederalEntityKeyCode,
    private var name: FederalEntityName,
    private val createdAt: FederalEntityCreateDate,
    private var updatedAt: FederalEntityUpdateDate?
) {
    companion object {
        fun create(keyCode: String, name: String) = FederalEntity(
            FederalEntityId.random(),
            FederalEntityKeyCode(keyCode),
            FederalEntityName(name),
            FederalEntityCreateDate.now(),
            null
        )

        fun from(id: String, keyCode: String, name: String, createdAt: Date, updatedAt: Date?) = FederalEntity(
            FederalEntityId.fromString(id),
            FederalEntityKeyCode(keyCode),
            FederalEntityName(name),
            FederalEntityCreateDate(createdAt),
            updatedAt?.let {
                if (it.before(createdAt))
                    throw InvalidFederalEntityUpdateDateError(it, createdAt)

                FederalEntityUpdateDate(it)
            }
        )
    }

    fun id() = id.value

    fun keyCode() = keyCode.value

    fun name() = name.value

    fun createdAt() = createdAt.value

    fun updatedAt() = updatedAt?.value

    fun changeName(name: String) {
        this.name = FederalEntityName(name)
        this.updatedAt = FederalEntityUpdateDate.now()
    }

    fun changeKeyCode(keyCode: String) {
        this.keyCode = FederalEntityKeyCode(keyCode)
        this.updatedAt = FederalEntityUpdateDate.now()
    }
}

data class FederalEntityId(val value: UUID) {
    companion object {
        fun random() = FederalEntityId(UUID.randomUUID())

        fun fromString(value: String) = try {
            FederalEntityId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidFederalEntityIdError(value, e)
        }
    }
}

data class FederalEntityKeyCode(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (Regex("[0-9]{2}").matches(value).not() || value == "00") {
            throw InvalidFederalEntityKeyCodeError(value)
        }
    }
}

data class FederalEntityName(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (value.isBlank()) {
            throw InvalidFederalEntityNameError(value)
        }
    }
}

data class FederalEntityCreateDate(val value: Date) {
    companion object {
        fun now() = FederalEntityCreateDate(Date())
    }
}

data class FederalEntityUpdateDate(val value: Date) {
    companion object {
        fun now() = FederalEntityUpdateDate(Date())
    }
}
