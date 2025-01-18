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

import arrow.core.Either
import java.util.*

abstract class InstrumentFileContentReader {
    abstract fun read(): ByteArray
}

class InstrumentImportData(
    private val statisticYear: Int,
    private val statisticMonth: Int,
    federalEntityKeyCode: String,
    municipalityKeyCode: String,
    agencyConsecutive: String,
    statisticTypeKeyCode: String,
    private val saved: Boolean,
    private val createdAt: Date,
    val instrumentFileContentReader: InstrumentFileContentReader
) {
    private val federalEntityKeyCode: String = federalEntityKeyCode.trim()
    private val municipalityKeyCode: String = municipalityKeyCode.trim()
    private val agencyConsecutive: String = agencyConsecutive.trim()
    private val statisticTypeKeyCode: String = statisticTypeKeyCode.trim()

    fun statisticYear(): Int {
        return statisticYear
    }

    fun statisticMonth(): Int {
        return statisticMonth
    }

    fun federalEntityKeyCode(): String {
        return federalEntityKeyCode
    }

    fun municipalityKeyCode(): String {
        return municipalityKeyCode
    }

    fun agencyConsecutive(): String {
        return agencyConsecutive
    }

    fun statisticTypeKeyCode(): String {
        return statisticTypeKeyCode
    }

    fun createdAt(): Date {
        return createdAt
    }

    fun saved(): Boolean {
        return saved
    }
}

enum class InstrumentImportDataFields(val value: String) {
    STATISTIC_YEAR("Statistic Year"),
    STATISTIC_MONTH("Statistic Month"),
    FEDERAL_ENTITY_KEY_CODE("Federal Entity KeyCode"),
    MUNICIPALITY_KEY_CODE("Municipality KeyCode"),
    AGENCY_CONSECUTIVE("Agency Consecutive"),
    STATISTIC_TYPE_KEY_CODE("Statistic Type KeyCode"),
    SAVED_IN_SIRESO("Saved in SIRESO"),
    CREATED_AT("Created At"),
    INSTRUMENT_FILE_LOCATION("Instrument File Location"),
    INSTRUMENT_FILE_CONTENT("Instrument File Content")
}

abstract class InstrumentImportInput

abstract class AccdbInstrumentImportInput : InstrumentImportInput()

interface InstrumentImportReader<I : InstrumentImportInput> {
    fun read(input: I): List<Either<InstrumentImportFieldNotFound, InstrumentImportData>>
}
