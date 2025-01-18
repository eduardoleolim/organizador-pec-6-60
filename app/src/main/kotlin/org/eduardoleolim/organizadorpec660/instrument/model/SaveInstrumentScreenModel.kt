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

package org.eduardoleolim.organizadorpec660.instrument.model

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import arrow.core.getOrElse
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.*
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.SimpleAgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.searchByMunicipalityId.SearchAgenciesByMunicipalityIdQuery
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.instrument.application.create.CreateInstrumentCommand
import org.eduardoleolim.organizadorpec660.instrument.application.searchById.SearchInstrumentByIdQuery
import org.eduardoleolim.organizadorpec660.instrument.application.update.UpdateInstrumentCommand
import org.eduardoleolim.organizadorpec660.instrument.data.EmptyInstrumentDataException
import org.eduardoleolim.organizadorpec660.instrument.views.InstrumentScreen
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.shared.composables.ResetFilePickerInteraction
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryNotRegisteredError
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import java.io.File
import java.text.DateFormatSymbols
import java.time.LocalDate

class SaveInstrumentScreenModel(
    private val navigator: Navigator,
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val tempDirectory: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    val filePickerInteractionSource = MutableInteractionSource()

    val statisticYears: List<Int> get() = (LocalDate.now().year downTo 1983).toList()

    val statisticMonths: List<Pair<Int, String>>
        get() = DateFormatSymbols().months.take(12).mapIndexed { index, month -> index + 1 to month.uppercase() }

    var federalEntities by mutableStateOf(emptyList<FederalEntityResponse>())
        private set

    var municipalities by mutableStateOf(emptyList<MunicipalityResponse>())
        private set

    var agencies by mutableStateOf(emptyList<AgencyResponse>())
        private set

    var statisticTypes by mutableStateOf(emptyList<StatisticTypeResponse>())
        private set

    var formState by mutableStateOf<InstrumentFormState>(InstrumentFormState.Idle)
        private set

    var instrument by mutableStateOf(InstrumentFormData())
        private set

    fun goBackToInstrumentView() {
        navigator.popUntil<InstrumentScreen, Screen>()
    }

    fun resetState() {
        formState = InstrumentFormState.Idle
    }

    fun searchInstrument(instrumentId: String?) {
        screenModelScope.launch(dispatcher) {
            instrument = if (instrumentId != null) {
                try {
                    val query = SearchInstrumentByIdQuery(instrumentId)
                    queryBus.ask(query).fold(
                        ifRight = { instrumentResponse ->
                            val instrumentFile = File(tempDirectory, "edit/${instrumentResponse.filename}.pdf").also {
                                it.parentFile.mkdirs()
                                it.writeBytes(instrumentResponse.instrumentFile.content)
                            }

                            with(instrumentResponse) {
                                InstrumentFormData(
                                    id = id,
                                    statisticYear = statisticYear,
                                    statisticMonth = statisticMonths.first { it.first == statisticMonth },
                                    statisticType = statisticType,
                                    federalEntity = federalEntity,
                                    municipality = municipality,
                                    agency = agency,
                                    instrumentFilePath = instrumentFile.absolutePath
                                )
                            }
                        },
                        ifLeft = {
                            InstrumentFormData()
                        }
                    )
                } catch (_: QueryNotRegisteredError) {
                    InstrumentFormData()
                }
            } else {
                InstrumentFormData()
            }
        }
    }

    fun updateInstrumentStatisticYear(statisticYear: Int?) {
        instrument = instrument.copy(statisticYear = statisticYear)
    }

    fun updateInstrumentStatisticMonth(statisticMonth: Pair<Int, String>?) {
        instrument = instrument.copy(statisticMonth = statisticMonth)
    }

    fun updateInstrumentFederalEntity(federalEntity: FederalEntityResponse?) {
        val isNewSelection = federalEntity?.id != instrument.federalEntity?.id
        instrument = instrument.copy(
            federalEntity = federalEntity,
            municipality = if (isNewSelection) null else instrument.municipality,
            agency = if (isNewSelection) null else instrument.agency,
            statisticType = if (isNewSelection) null else instrument.statisticType
        )
    }

    fun updateInstrumentMunicipality(municipality: MunicipalityResponse?) {
        val isNewSelection = municipality?.id != instrument.municipality?.id
        instrument = instrument.copy(
            municipality = municipality?.let {
                SimpleMunicipalityResponse(it.id, it.name, it.keyCode, it.federalEntity.id, it.createdAt, it.updatedAt)
            },
            agency = if (isNewSelection) null else instrument.agency,
            statisticType = if (isNewSelection) null else instrument.statisticType
        )
    }

    fun updateInstrumentAgency(agency: AgencyResponse?) {
        val isNewSelection = agency?.id != instrument.agency?.id
        instrument = instrument.copy(
            agency = agency?.let { SimpleAgencyResponse(it.id, it.name, it.consecutive, it.createdAt, it.updatedAt) },
            statisticType = if (isNewSelection) null else instrument.statisticType
        )
    }

    fun updateInstrumentStatisticType(statisticType: StatisticTypeResponse?) {
        instrument = instrument.copy(statisticType = statisticType)
    }

    fun updateInstrumentInstrumentFilePath(instrumentFilePath: String?) {
        instrument = instrument.copy(instrumentFilePath = instrumentFilePath)
    }

    fun searchAllFederalEntities() {
        screenModelScope.launch(dispatcher) {
            federalEntities = try {
                val query = SearchFederalEntitiesByTermQuery()
                queryBus.ask(query).map { it.federalEntities }.getOrElse { emptyList() }
            } catch (_: QueryNotRegisteredError) {
                emptyList()
            }
        }
    }

    fun searchMunicipalities(federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            municipalities = if (federalEntityId != null) {
                try {
                    val query = SearchMunicipalitiesByTermQuery(federalEntityId)
                    queryBus.ask(query).map { it.municipalities }.getOrElse { emptyList() }
                } catch (_: QueryNotRegisteredError) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    fun searchAgencies(municipalityId: String?) {
        screenModelScope.launch(dispatcher) {
            agencies = if (municipalityId != null) {
                try {
                    val query = SearchAgenciesByMunicipalityIdQuery(municipalityId)
                    queryBus.ask(query).map { it.agencies }.getOrElse { emptyList() }
                } catch (_: QueryNotRegisteredError) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    fun searchStatisticTypes(agencyId: String?) {
        statisticTypes = try {
            agencies.first { it.id == agencyId }.statisticTypes
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveInstrument() {
        screenModelScope.launch(dispatcher) {
            val (id, statisticYear, statisticMonth, statisticType, _, municipality, agency, instrumentFilePath) = instrument
            val isYearUnselected = statisticYear == null
            val isMonthUnselected = statisticMonth == null
            val isMunicipalityUnselected = municipality == null
            val isAgencyUnselected = agency == null
            val isStatisticTypeUnselected = statisticType == null
            val isInstrumentFileUnselected = instrumentFilePath == null

            formState = InstrumentFormState.InProgress
            delay(500)

            if (isYearUnselected || isMonthUnselected || isMunicipalityUnselected || isAgencyUnselected || isStatisticTypeUnselected || isInstrumentFileUnselected) {
                formState = InstrumentFormState.Error(
                    EmptyInstrumentDataException(
                        isYearUnselected,
                        isMonthUnselected,
                        isMunicipalityUnselected,
                        isAgencyUnselected,
                        isStatisticTypeUnselected,
                        isInstrumentFileUnselected
                    )
                )
                return@launch
            }

            if (id == null) {
                createInstrument(
                    statisticYear,
                    statisticMonth.first,
                    municipality.id,
                    agency.id,
                    statisticType.id,
                    instrumentFilePath
                )
            } else {
                updateInstrument(
                    id,
                    statisticYear,
                    statisticMonth.first,
                    municipality.id,
                    agency.id,
                    statisticType.id,
                    instrumentFilePath
                )
            }
        }
    }

    private suspend fun createInstrument(
        year: Int,
        month: Int,
        municipalityId: String,
        agencyId: String,
        statisticTypeId: String,
        documentPath: String
    ) {
        withContext(dispatcher) {
            formState = try {
                val document = File(documentPath).readBytes()
                val command = CreateInstrumentCommand(year, month, agencyId, statisticTypeId, municipalityId, document)
                commandBus.dispatch(command).fold(
                    ifRight = {
                        filePickerInteractionSource.emit(ResetFilePickerInteraction)
                        instrument = instrument.copy(
                            municipality = null,
                            agency = null,
                            statisticType = null,
                            instrumentFilePath = null
                        )
                        InstrumentFormState.SuccessCreate
                    },
                    ifLeft = {
                        InstrumentFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                InstrumentFormState.Error(e.cause!!)
            }
        }
    }

    private suspend fun updateInstrument(
        instrumentId: String,
        year: Int,
        month: Int,
        municipalityId: String,
        agencyId: String,
        statisticTypeId: String,
        documentPath: String
    ) {
        withContext(dispatcher) {
            formState = try {
                val document = File(documentPath).readBytes()
                val command = UpdateInstrumentCommand(
                    instrumentId,
                    year,
                    month,
                    agencyId,
                    statisticTypeId,
                    municipalityId,
                    document
                )
                commandBus.dispatch(command).fold(
                    ifRight = {
                        InstrumentFormState.SuccessEdit
                    },
                    ifLeft = {
                        InstrumentFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                InstrumentFormState.Error(e.cause!!)
            }
        }
    }
}
