package org.eduardoleolim.organizadorpec660.core.statisticType.domain

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeId
import java.util.*

class StatisticType private constructor(
    private val id: StatisticTypeId,
    private var keyCode: StatisticTypeKeyCode,
    private var name: StatisticTypeName,
    private var instrumentTypeIds: MutableList<InstrumentTypeId>,
    private val createdAt: StatisticTypeCreateDate,
    private var updatedAt: StatisticTypeUpdateDate?
) {
    init {
        instrumentTypeIds = instrumentTypeIds.distinct().toMutableList()
        validate()
    }

    private fun validate() {
        if (instrumentTypeIds.isEmpty()) {
            throw NotEnoughInstrumentTypesError()
        }
    }

    companion object {
        fun create(keyCode: String, name: String, instrumentTypeIds: MutableList<String>) = StatisticType(
            StatisticTypeId.random(),
            StatisticTypeKeyCode(keyCode),
            StatisticTypeName(name),
            instrumentTypeIds.map(InstrumentTypeId::fromString).toMutableList(),
            StatisticTypeCreateDate.now(),
            null
        )

        fun from(
            id: String,
            keyCode: String,
            name: String,
            instrumentTypeIds: MutableList<String>,
            createdAt: Date,
            updatedAt: Date?
        ) = StatisticType(
            StatisticTypeId.fromString(id),
            StatisticTypeKeyCode(keyCode),
            StatisticTypeName(name),
            instrumentTypeIds.map(InstrumentTypeId::fromString).toMutableList(),
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

    fun instrumentTypeIds() = instrumentTypeIds.toList()

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

    fun addInstrumentTypeId(id: String) {
        InstrumentTypeId.fromString(id).let {
            if (instrumentTypeIds.contains(it))
                return

            if (instrumentTypeIds.add(it))
                this.updatedAt = StatisticTypeUpdateDate.now()
        }
    }

    fun removeInstrumentTypeId(id: String) {
        if (instrumentTypeIds.size == 1 && instrumentTypeIds.first().toString() == id)
            throw NotEnoughInstrumentTypesError()

        if (instrumentTypeIds.removeIf { it.value.toString() == id }) {
            this.updatedAt = StatisticTypeUpdateDate.now()
        }
    }

    fun changeInstrumentTypeIds(instrumentTypeIds: List<String>) {
        instrumentTypeIds.distinct().let {
            if (it.isEmpty())
                throw NotEnoughInstrumentTypesError()

            this.instrumentTypeIds.clear()
            this.instrumentTypeIds.addAll(it.map(InstrumentTypeId::fromString))
            this.updatedAt = StatisticTypeUpdateDate.now()
        }
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
        if (Regex("[0-9]{2}").matches(value).not()) {
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
