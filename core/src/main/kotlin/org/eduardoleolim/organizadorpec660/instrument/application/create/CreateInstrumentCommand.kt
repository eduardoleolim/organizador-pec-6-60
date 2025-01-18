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

package org.eduardoleolim.organizadorpec660.instrument.application.create

import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import java.util.*

class CreateInstrumentCommand(
    private val statisticYear: Int,
    private val statisticMonth: Int,
    agencyId: String,
    statisticTypeId: String,
    municipalityId: String,
    private val file: ByteArray
) : Command<InstrumentError, UUID> {
    private val agencyId = agencyId.trim()
    private val statisticTypeId = statisticTypeId.trim()
    private val municipalityId = municipalityId.trim()

    fun statisticYear(): Int {
        return statisticYear
    }

    fun statisticMonth(): Int {
        return statisticMonth
    }

    fun agencyId(): String {
        return agencyId
    }

    fun statisticTypeId(): String {
        return statisticTypeId
    }

    fun municipalityId(): String {
        return municipalityId
    }

    fun file(): ByteArray {
        return file
    }
}
