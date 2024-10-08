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

package org.eduardoleolim.organizadorpec660.instrument.application.importer

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.instrument.domain.*
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeRepository
import java.util.*

data class InstrumentImportWarning(val index: Int, val error: Throwable)

class InstrumentFromV1Importer<I : InstrumentImportInput>(
    private val instrumentRepository: InstrumentRepository,
    private val federalEntityRepository: FederalEntityRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val agencyRepository: AgencyRepository,
    private val statisticTypeRepository: StatisticTypeRepository,
    private val reader: InstrumentImportReader<I>
) {
    private val federalEntitiesCache = mutableMapOf<String, FederalEntity>()
    private val municipalitiesCache = mutableMapOf<String, Municipality>()
    private val agenciesCache = mutableMapOf<String, Agency>()
    private val statisticTypesCache = mutableMapOf<String, StatisticType>()

    fun import(input: I, overrideIfExists: Boolean): Either<InstrumentError, List<InstrumentImportWarning>> {
        val warnings = mutableListOf<InstrumentImportWarning>()
        val instruments = reader.read(input)

        instruments.forEachIndexed { index, either ->
            either.fold(
                ifRight = { data ->
                    val statisticYear = data.statisticYear()
                    val statisticMonth = data.statisticMonth()
                    val federalEntityKeyCode = data.federalEntityKeyCode()
                    val municipalityKeyCode = data.municipalityKeyCode()
                    val agencyConsecutive = data.agencyConsecutive()
                    val statisticTypeKeyCode = data.statisticTypeKeyCode()
                    val savedInSireso = data.saved()
                    val createdAt = data.createdAt()
                    val instrumentFileContent = data.instrumentFileContent()

                    val federalEntity = federalEntitiesCache[federalEntityKeyCode]
                        ?: searchFederalEntity(federalEntityKeyCode)?.also {
                            federalEntitiesCache[federalEntityKeyCode] = it
                        }

                    if (federalEntity == null) {
                        warnings.add(InstrumentImportWarning(index, FederalEntityNotFoundError(federalEntityKeyCode)))
                        return@forEachIndexed
                    }

                    val municipalityKey = "${federalEntity.id()}-$municipalityKeyCode"
                    val municipality = municipalitiesCache[municipalityKey]
                        ?: searchMunicipality(federalEntity.id(), municipalityKeyCode)?.also {
                            municipalitiesCache[municipalityKey] = it
                        }

                    if (municipality == null) {
                        warnings.add(InstrumentImportWarning(index, MunicipalityNotFoundError(municipalityKeyCode)))
                        return@forEachIndexed
                    }

                    val agencyKey = "${municipality.id()}-$agencyConsecutive"
                    val agency = agenciesCache[agencyKey]
                        ?: searchAgency(municipality.id(), agencyConsecutive)?.also {
                            agenciesCache[agencyKey] = it
                        }

                    if (agency == null) {
                        warnings.add(InstrumentImportWarning(index, AgencyNotFoundError(agencyConsecutive)))
                        return@forEachIndexed
                    }

                    val statisticType = statisticTypesCache[statisticTypeKeyCode]
                        ?: searchStatisticType(statisticTypeKeyCode)?.also {
                            statisticTypesCache[statisticTypeKeyCode] = it
                        }

                    if (statisticType == null) {
                        warnings.add(InstrumentImportWarning(index, StatisticTypeNotFoundError(statisticTypeKeyCode)))
                        return@forEachIndexed
                    }

                    val instrument = searchInstrument(
                        statisticYear,
                        statisticMonth,
                        agency.id().toString(),
                        statisticType.id().toString(),
                        municipality.id().toString()
                    )

                    when {
                        instrument != null && overrideIfExists -> {
                            instrument.apply {
                                changeStatisticYear(statisticYear)
                                changeStatisticMonth(statisticMonth)
                                changeMunicipalityId(municipality.id().toString())
                                changeAgencyId(agency.id().toString())
                                changeStatisticTypeId(statisticType.id().toString())
                                if (savedInSireso) {
                                    saveInSIRESO()
                                } else {
                                    unSaveInSIRESO()
                                }
                            }
                            val instrumentFile = searchInstrumentFile(instrument.instrumentFileId().toString()).apply {
                                changeContent(instrumentFileContent)
                            }

                            instrumentRepository.save(instrument, instrumentFile)
                        }

                        instrument == null -> {
                            val instrumentFile = InstrumentFile.create(instrumentFileContent)
                            Instrument.createFromV1(
                                statisticYear,
                                statisticMonth,
                                instrumentFile.id().toString(),
                                agency.id().toString(),
                                statisticType.id().toString(),
                                municipality.id().toString(),
                                savedInSireso,
                                createdAt
                            ).let {
                                instrumentRepository.save(it, instrumentFile)
                            }
                        }
                    }
                },
                ifLeft = { error ->
                    warnings.add(InstrumentImportWarning(index, error))
                }
            )
        }

        federalEntitiesCache.clear()
        municipalitiesCache.clear()
        agenciesCache.clear()
        statisticTypesCache.clear()

        if (warnings.size == instruments.size) {
            return Either.Left(CanNotImportInstrumentsError())
        }

        return Either.Right(warnings)
    }

    private fun searchFederalEntity(keyCode: String) = FederalEntityCriteria.keyCodeCriteria(keyCode).let {
        federalEntityRepository.matching(it).firstOrNull()
    }

    private fun searchMunicipality(federalEntityId: UUID, municipalityKeyCode: String) =
        MunicipalityCriteria.keyCodeAndFederalEntityIdCriteria(municipalityKeyCode, federalEntityId.toString()).let {
            municipalityRepository.matching(it).firstOrNull()
        }

    private fun searchAgency(municipalityId: UUID, consecutive: String) =
        AgencyCriteria.anotherConsecutiveCriteria(consecutive, municipalityId.toString()).let {
            agencyRepository.matching(it).firstOrNull()
        }

    private fun searchStatisticType(keyCode: String) = StatisticTypeCriteria.keyCodeCriteria(keyCode).let {
        statisticTypeRepository.matching(it).firstOrNull()
    }

    private fun searchInstrument(
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String
    ) = InstrumentCriteria.otherInstrumentCriteria(
        statisticYear,
        statisticMonth,
        agencyId,
        statisticTypeId,
        municipalityId
    ).let {
        instrumentRepository.matching(it).firstOrNull()
    }

    private fun searchInstrumentFile(instrumentFileId: String) =
        instrumentRepository.searchInstrumentFile(instrumentFileId)!!
}
