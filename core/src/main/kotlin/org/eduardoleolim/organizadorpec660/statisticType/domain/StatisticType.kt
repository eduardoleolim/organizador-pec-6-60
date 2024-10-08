/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.statisticType.domain

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
