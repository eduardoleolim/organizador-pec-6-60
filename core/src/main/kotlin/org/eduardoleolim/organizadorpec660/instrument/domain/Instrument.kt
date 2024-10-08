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

package org.eduardoleolim.organizadorpec660.instrument.domain

import org.eduardoleolim.organizadorpec660.agency.domain.AgencyId
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityId
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeId
import java.util.*

class Instrument private constructor(
    private val id: InstrumentId,
    private var statisticYear: InstrumentStatisticYear,
    private var statisticMonth: InstrumentStatisticMonth,
    private var saved: InstrumentSaved,
    private val instrumentFileId: InstrumentFileId,
    private var agencyId: AgencyId,
    private var statisticTypeId: StatisticTypeId,
    private var municipalityId: MunicipalityId,
    private val createdAt: InstrumentCreateDate,
    private var updatedAt: InstrumentUpdateDate?
) {
    companion object {
        fun create(
            statisticYear: Int,
            statisticMonth: Int,
            instrumentFileId: String,
            agencyId: String,
            statisticTypeId: String,
            municipalityId: String
        ) = Instrument(
            InstrumentId.random(),
            InstrumentStatisticYear(statisticYear),
            InstrumentStatisticMonth(statisticMonth),
            InstrumentSaved(false),
            InstrumentFileId.fromString(instrumentFileId),
            AgencyId.fromString(agencyId),
            StatisticTypeId.fromString(statisticTypeId),
            MunicipalityId.fromString(municipalityId),
            InstrumentCreateDate.now(),
            null
        )

        fun createFromV1(
            statisticYear: Int,
            statisticMonth: Int,
            instrumentFileId: String,
            agencyId: String,
            statisticTypeId: String,
            municipalityId: String,
            saved: Boolean,
            createdAt: Date
        ) = Instrument(
            InstrumentId.random(),
            InstrumentStatisticYear(statisticYear),
            InstrumentStatisticMonth(statisticMonth),
            InstrumentSaved(saved),
            InstrumentFileId.fromString(instrumentFileId),
            AgencyId.fromString(agencyId),
            StatisticTypeId.fromString(statisticTypeId),
            MunicipalityId.fromString(municipalityId),
            InstrumentCreateDate(createdAt),
            null
        )

        fun from(
            id: String,
            statisticYear: Int,
            statisticMonth: Int,
            saved: Boolean,
            instrumentFileId: String,
            agencyId: String,
            statisticTypeId: String,
            municipalityId: String,
            createdAt: Date,
            updatedAt: Date?
        ) = Instrument(
            InstrumentId.fromString(id),
            InstrumentStatisticYear(statisticYear),
            InstrumentStatisticMonth(statisticMonth),
            InstrumentSaved(saved),
            InstrumentFileId.fromString(instrumentFileId),
            AgencyId.fromString(agencyId),
            StatisticTypeId.fromString(statisticTypeId),
            MunicipalityId.fromString(municipalityId),
            InstrumentCreateDate(createdAt),
            updatedAt?.let {
                if (it.before(createdAt))
                    throw InvalidInstrumentUpdateDateError(it, createdAt)

                InstrumentUpdateDate(it)
            }
        )
    }

    fun id() = id.value

    fun statisticYear() = statisticYear.value

    fun statisticMonth() = statisticMonth.value

    fun savedInSIRESO() = saved.value

    fun instrumentFileId() = instrumentFileId.value

    fun agencyId() = agencyId.value

    fun statisticTypeId() = statisticTypeId.value

    fun municipalityId() = municipalityId.value

    fun createdAt() = createdAt.value

    fun updatedAt() = updatedAt?.value

    fun changeStatisticYear(statisticYear: Int) {
        this.statisticYear = InstrumentStatisticYear(statisticYear)
        this.updatedAt = InstrumentUpdateDate.now()
    }

    fun changeStatisticMonth(statisticMonth: Int) {
        this.statisticMonth = InstrumentStatisticMonth(statisticMonth)
        this.updatedAt = InstrumentUpdateDate.now()
    }

    fun saveInSIRESO() {
        this.saved = InstrumentSaved(true)
        this.updatedAt = InstrumentUpdateDate.now()
    }

    fun unSaveInSIRESO() {
        this.saved = InstrumentSaved(false)
        this.updatedAt = InstrumentUpdateDate.now()
    }

    fun changeAgencyId(agencyId: String) {
        this.agencyId = AgencyId.fromString(agencyId)
        this.updatedAt = InstrumentUpdateDate.now()
    }

    fun changeStatisticTypeId(statisticTypeId: String) {
        this.statisticTypeId = StatisticTypeId.fromString(statisticTypeId)
        this.updatedAt = InstrumentUpdateDate.now()
    }

    fun changeMunicipalityId(municipalityId: String) {
        this.municipalityId = MunicipalityId.fromString(municipalityId)
        this.updatedAt = InstrumentUpdateDate.now()
    }
}

data class InstrumentId(val value: UUID) {
    companion object {
        fun random() = InstrumentId(UUID.randomUUID())

        fun fromString(value: String) = try {
            InstrumentId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidInstrumentIdError(value, e)
        }
    }
}

data class InstrumentStatisticYear(val value: Int) {
    init {
        validate(value)
    }

    private fun validate(value: Int) {
        if (value < 0)
            throw InvalidInstrumentStatisticYearError(value)
    }
}

data class InstrumentStatisticMonth(val value: Int) {
    init {
        validate(value)
    }

    private fun validate(value: Int) {
        if ((1..12).contains(value).not())
            throw InvalidInstrumentStatisticMonthError(value)
    }
}

data class InstrumentSaved(val value: Boolean)

data class InstrumentCreateDate(val value: Date) {
    companion object {
        fun now() = InstrumentCreateDate(Date())
    }
}

data class InstrumentUpdateDate(val value: Date) {
    companion object {
        fun now() = InstrumentUpdateDate(Date())
    }
}
