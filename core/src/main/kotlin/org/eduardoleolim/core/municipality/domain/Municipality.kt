package org.eduardoleolim.core.municipality.domain

import org.eduardoleolim.core.federalEntity.domain.FederalEntity
import java.util.*

class Municipality private constructor(
    private val id: MunicipalityId,
    private var keyCode: MunicipalityKeyCode,
    private var name: MunicipalityName,
    private var federalEntity: FederalEntity,
    private val createdAt: MunicipalityCreateDate,
    private var updatedAt: MunicipalityUpdateDate?
) {
    companion object {
        fun create(keyCode: String, name: String, federalEntity: FederalEntity) = Municipality(
            MunicipalityId.random(),
            MunicipalityKeyCode(keyCode),
            MunicipalityName(name),
            federalEntity,
            MunicipalityCreateDate.now(),
            null
        )

        fun from(
            id: String,
            keyCode: String,
            name: String,
            federalEntity: FederalEntity,
            createdAt: Date,
            updatedAt: Date?
        ) = Municipality(
            MunicipalityId.fromString(id),
            MunicipalityKeyCode(keyCode),
            MunicipalityName(name),
            federalEntity,
            MunicipalityCreateDate(createdAt),
            updatedAt?.let {
                if (it.before(createdAt)) throw InvalidMunicipalityUpdateDateError(it, createdAt)
                MunicipalityUpdateDate(it)
            }
        )
    }

    fun id() = id.value

    fun keyCode() = keyCode.value

    fun name() = name.value

    fun federalEntity() = federalEntity

    fun createdAt() = createdAt.value

    fun updatedAt() = updatedAt?.value

    fun changeName(name: String) {
        this.name = MunicipalityName(name)
        this.updatedAt = MunicipalityUpdateDate.now()
    }

    fun changeKeyCode(keyCode: String) {
        this.keyCode = MunicipalityKeyCode(keyCode)
        this.updatedAt = MunicipalityUpdateDate.now()
    }

    fun changeFederalEntity(federalEntity: FederalEntity) {
        this.federalEntity = federalEntity
        this.updatedAt = MunicipalityUpdateDate.now()
    }
}

data class MunicipalityId(val value: UUID) {
    companion object {
        fun random() = MunicipalityId(UUID.randomUUID())

        fun fromString(value: String) = try {
            MunicipalityId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidMunicipalityIdError(value, e)
        }
    }
}

data class MunicipalityKeyCode(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (Regex("[0-9]{3}").matches(value).not()) {
            throw InvalidMunicipalityKeyCodeError(value)
        }
    }
}

data class MunicipalityName(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (value.isBlank()) {
            throw InvalidMunicipalityNameError(value)
        }
    }
}

data class MunicipalityCreateDate(val value: Date) {
    companion object {
        fun now() = MunicipalityCreateDate(Date())
    }
}

data class MunicipalityUpdateDate(val value: Date) {
    companion object {
        fun now() = MunicipalityUpdateDate(Date())
    }
}