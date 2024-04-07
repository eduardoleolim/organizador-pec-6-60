package org.eduardoleolim.organizadorpec660.core.statisticType.domain

import java.util.*

class StatisticType private constructor(
    private val id: StatisticTypeId,
    private var keyCode: StatisticTypeKeyCode,
    private var name: StatisticTypeName,
    private val createdAt: StatisticTypeCreateDate,
    private var updatedAt: StatisticTypeUpdateDate?
) {
    companion object {
        fun create(keyCode: String, name: String) = StatisticType(
            StatisticTypeId.random(),
            StatisticTypeKeyCode(keyCode),
            StatisticTypeName(name),
            StatisticTypeCreateDate.now(),
            null
        )

        fun from(
            id: String,
            keyCode: String,
            name: String,
            createdAt: Date,
            updatedAt: Date?
        ) = StatisticType(
            StatisticTypeId.fromString(id),
            StatisticTypeKeyCode(keyCode),
            StatisticTypeName(name),
            StatisticTypeCreateDate(createdAt),
            updatedAt?.let {
                if (it.before(createdAt))
                    throw InvalidStatisticTypeUpdateDateError(it, createdAt)

                StatisticTypeUpdateDate(it)
            }
        )
    }

    fun id() = id.value

    fun keyCode() = keyCode.value

    fun name() = name.value

    fun createdAt() = createdAt.value

    fun updatedAt() = updatedAt?.value

    fun changeKeyCode(keyCode: String) {
        this.keyCode = StatisticTypeKeyCode(keyCode)
        this.updatedAt = StatisticTypeUpdateDate.now()
    }

    fun changeName(name: String) {
        this.name = StatisticTypeName(name)
        this.updatedAt = StatisticTypeUpdateDate.now()
    }
}

data class StatisticTypeId(val value: UUID) {
    companion object {
        fun random() = StatisticTypeId(UUID.randomUUID())

        fun fromString(value: String) = try {
            StatisticTypeId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidStatisticTypeIdError(value, e)
        }
    }
}

data class StatisticTypeKeyCode(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (Regex("[0-9]{3}").matches(value).not()) {
            throw InvalidStatisticTypeKeyCodeError(value)
        }
    }
}

data class StatisticTypeName(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (value.isBlank()) {
            throw InvalidStatisticTypeNameError(value)
        }
    }
}

data class StatisticTypeCreateDate(val value: Date) {
    companion object {
        fun now() = StatisticTypeCreateDate(Date())
    }
}

data class StatisticTypeUpdateDate(val value: Date) {
    companion object {
        fun now() = StatisticTypeUpdateDate(Date())
    }
}
