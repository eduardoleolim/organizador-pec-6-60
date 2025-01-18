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

package org.eduardoleolim.organizadorpec660.instrument.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.instrument.domain.*
import org.eduardoleolim.organizadorpec660.instrument.domain.Instrument
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentFile
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.support.sqlite.insertOrUpdate
import java.io.File
import java.time.LocalDateTime

class KtormInstrumentRepository(
    private val database: Database,
    private val instrumentPath: String
) : InstrumentRepository {
    private val instruments = Instruments("i")
    private val instrumentFiles = InstrumentFiles("if")
    private val agencies = Agencies("ag")
    private val statisticTypes = StatisticTypes("st")
    private val federalEntities = FederalEntities("fe")
    private val municipalities = Municipalities("m")
    private val criteriaParser = KtormInstrumentsCriteriaParser(
        database,
        instruments,
        instrumentFiles,
        agencies,
        statisticTypes,
        federalEntities,
        municipalities
    )

    override fun matching(criteria: Criteria): List<Instrument> {
        val query = criteriaParser.selectQuery(criteria)

        return query.map { rowSet ->
            instruments.createEntity(rowSet).let {
                Instrument.from(
                    it.id,
                    it.statisticYear,
                    it.statisticMonth,
                    it.saved,
                    it.instrumentFile.id,
                    it.agency.id,
                    it.statisticType.id,
                    it.municipality.id,
                    it.createdAt.toDate(),
                    it.updatedAt?.toDate()
                )
            }
        }
    }

    override fun searchFileById(id: String): InstrumentFile? {
        val instrumentFile = database.sequenceOf(instrumentFiles).firstOrNull { it.id eq id }

        return instrumentFile?.let { entity ->
            val byteArray = File(entity.path).readBytes()

            InstrumentFile.from(entity.id, byteArray)
        }
    }

    override fun searchInstrumentFile(instrumentFileId: String): InstrumentFile? {
        val instrumentFile = database.from(instrumentFiles)
            .select(instrumentFiles.columns)
            .where { instrumentFiles.id eq instrumentFileId }
            .map { instrumentFiles.createEntity(it) }
            .firstOrNull()

        return instrumentFile?.let { entity ->
            val byteArray = File(entity.path).readBytes()

            InstrumentFile.from(entity.id, byteArray)
        }
    }

    override fun count(criteria: Criteria): Int {
        val query = criteriaParser.countQuery(criteria)

        return query.rowSet.let {
            it.next()
            it.getInt(1)
        }
    }

    override fun save(instrument: Instrument, instrumentFile: InstrumentFile?) {
        database.useTransaction {
            var file: File? = null
            val exists = count(InstrumentCriteria.idCriteria(instrument.id().toString())) > 0

            if (exists.not() && instrumentFile == null)
                throw InstrumentFileRequiredError()

            instrumentFile?.let {
                val id = instrumentFile.id().toString()
                file = File(instrumentPath, "$id.pdf")

                database.insertOrUpdate(instrumentFiles) {
                    set(it.id, id)
                    set(it.path, file!!.absolutePath)

                    onConflict(it.id) {
                        set(it.path, file!!.absolutePath)
                    }
                }

                val saveResult = file!!.runCatching {
                    parentFile.mkdirs()
                    writeBytes(instrumentFile.content())
                }

                if (saveResult.isFailure)
                    throw InstrumentFileFailSaveError(saveResult.exceptionOrNull())
            }

            try {
                database.insertOrUpdate(instruments) {
                    set(it.id, instrument.id().toString())
                    set(it.statisticYear, instrument.statisticYear())
                    set(it.statisticMonth, instrument.statisticMonth())
                    set(it.saved, instrument.savedInSIRESO())
                    set(it.createdAt, instrument.createdAt().toLocalDateTime())
                    set(it.agencyId, instrument.agencyId().toString())
                    set(it.statisticTypeId, instrument.statisticTypeId().toString())
                    set(it.municipalityId, instrument.municipalityId().toString())
                    set(it.instrumentFileId, instrument.instrumentFileId().toString())

                    onConflict(it.id) {
                        set(it.statisticYear, instrument.statisticYear())
                        set(it.statisticMonth, instrument.statisticMonth())
                        set(it.saved, instrument.savedInSIRESO())
                        set(it.updatedAt, instrument.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                        set(it.agencyId, instrument.agencyId().toString())
                        set(it.statisticTypeId, instrument.statisticTypeId().toString())
                        set(it.municipalityId, instrument.municipalityId().toString())
                    }
                }
            } catch (e: Throwable) {
                if (exists.not()) {
                    file?.delete()
                }
                throw e
            }
        }
    }

    override fun delete(instrumentId: String) {
        database.useTransaction {
            val instrument = matching(InstrumentCriteria.idCriteria(instrumentId)).firstOrNull()
                ?: throw InstrumentNotFoundError(instrumentId)
            val instrumentFilePath = database.from(instrumentFiles)
                .select()
                .where { instrumentFiles.id eq instrument.instrumentFileId().toString() }
                .map { it[instrumentFiles.path] }
                .first()

            database.delete(instruments) {
                it.id eq instrument.id().toString()
            }

            database.delete(instrumentFiles) {
                it.id eq instrument.instrumentFileId().toString()
            }

            instrumentFilePath?.let { path ->
                File(path).delete()
            }
        }
    }
}
