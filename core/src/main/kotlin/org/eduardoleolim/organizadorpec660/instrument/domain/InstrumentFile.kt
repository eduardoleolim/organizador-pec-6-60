/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
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
