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

package org.eduardoleolim.organizadorpec660.instrument.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import arrow.core.getOrElse
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.searchByMunicipalityId.SearchAgenciesByMunicipalityIdQuery
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.instrument.application.InstrumentsResponse
import org.eduardoleolim.organizadorpec660.instrument.application.delete.DeleteInstrumentCommand
import org.eduardoleolim.organizadorpec660.instrument.application.importer.ImportInstrumentsFromV1Command
import org.eduardoleolim.organizadorpec660.instrument.application.save.UpdateInstrumentAsNotSavedCommand
import org.eduardoleolim.organizadorpec660.instrument.application.save.UpdateInstrumentAsSavedCommand
import org.eduardoleolim.organizadorpec660.instrument.application.searchById.SearchInstrumentByIdQuery
import org.eduardoleolim.organizadorpec660.instrument.application.searchByTerm.SearchInstrumentsByTermQuery
import org.eduardoleolim.organizadorpec660.instrument.infrastructure.services.V1AccdbInstrumentImportInput
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryNotRegisteredError
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.inst_copy_notification_message
import org.eduardoleolim.organizadorpec660.shared.resources.inst_copy_notification_title
import org.eduardoleolim.organizadorpec660.shared.router.HomeProvider
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.jetbrains.compose.resources.getString
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.File

sealed class InstrumentDeleteState {
    data object Idle : InstrumentDeleteState()
    data object InProgress : InstrumentDeleteState()
    data object Success : InstrumentDeleteState()
    data class Error(val error: Throwable) : InstrumentDeleteState()
}

@OptIn(FlowPreview::class)
class InstrumentScreenModel(
    private val navigator: Navigator,
    private val trayState: TrayState,
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val tempDirectory: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    private val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard

    var screenState by mutableStateOf(InstrumentScreenState())
        private set

    private val _searchParameters = MutableStateFlow(InstrumentSearchParameters())

    val searchParameters: StateFlow<InstrumentSearchParameters> = _searchParameters

    var instruments by mutableStateOf(InstrumentsResponse(emptyList(), 0, null, null))
        private set

    var federalEntities by mutableStateOf(emptyList<FederalEntityResponse>())
        private set

    var municipalities by mutableStateOf(emptyList<MunicipalityResponse>())
        private set

    var agencies by mutableStateOf(emptyList<AgencyResponse>())
        private set

    var statisticTypes by mutableStateOf(emptyList<StatisticTypeResponse>())
        private set

    var deleteState by mutableStateOf<InstrumentDeleteState>(InstrumentDeleteState.Idle)
        private set

    var importState by mutableStateOf<InstrumentImportState>(InstrumentImportState.Idle)
        private set

    init {
        screenModelScope.launch(dispatcher) {
            _searchParameters
                .debounce(500)
                .collectLatest {
                    fetchInstruments(it)
                }
        }
    }

    fun setInitialMode() {
        screenModelScope.launch(dispatcher) {
            screenState = InstrumentScreenState()
            val limit = screenState.tableState.pageSize
            val offset = screenState.tableState.pageIndex * limit
            _searchParameters.value = InstrumentSearchParameters(limit = limit, offset = offset)
            fetchInstruments(_searchParameters.value)
            fetchAllFederalEntities()
            fetchAllStatisticTypes()
            municipalities = emptyList()
            agencies = emptyList()
        }
    }

    fun showImportExportSelector() {
        screenState = screenState.copy(
            showImportExportSelector = true,
            showImportModal = false,
            showExportModal = false
        )
    }

    fun showImportModal() {
        screenState = screenState.copy(
            showImportExportSelector = false,
            showImportModal = true,
            showExportModal = false
        )
    }

    fun showExportModal() {
        screenState = screenState.copy(
            showImportExportSelector = false,
            showImportModal = false,
            showExportModal = true
        )
    }

    fun resetImportModal() {
        importState = InstrumentImportState.Idle
    }

    fun searchAllFederalEntities() {
        screenModelScope.launch(dispatcher) {
            fetchAllFederalEntities()
        }
    }

    private suspend fun fetchAllFederalEntities() {
        withContext(dispatcher) {
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

    fun searchAllStatisticTypes() {
        screenModelScope.launch(dispatcher) {
            fetchAllStatisticTypes()
        }
    }

    private suspend fun fetchAllStatisticTypes() {
        withContext(dispatcher) {
            statisticTypes = try {
                queryBus.ask(SearchStatisticTypesByTermQuery()).map { it.statisticTypes }.getOrElse { emptyList() }
            } catch (_: QueryNotRegisteredError) {
                emptyList()
            }
        }
    }

    fun navigateToSaveInstrumentView(instrumentId: String?) {
        navigator.push(ScreenRegistry.get(HomeProvider.SaveInstrumentScreen(instrumentId)))
    }

    fun navigateToShowInstrumentDetailsView(instrumentId: String) {
        navigator.push(ScreenRegistry.get(HomeProvider.ShowInstrumentDetailsScreen(instrumentId)))
    }

    fun searchInstruments(
        search: String = searchParameters.value.search,
        federalEntity: FederalEntityResponse? = searchParameters.value.federalEntity,
        municipality: MunicipalityResponse? = searchParameters.value.municipality,
        agency: AgencyResponse? = searchParameters.value.agency,
        statisticType: StatisticTypeResponse? = searchParameters.value.statisticType,
        statisticYear: Int? = searchParameters.value.statisticYear,
        statisticMonth: Pair<Int, String>? = searchParameters.value.statisticMonth,
        orders: List<HashMap<String, String>> = searchParameters.value.orders,
        limit: Int? = searchParameters.value.limit,
        offset: Int? = searchParameters.value.offset,
    ) {
        _searchParameters.value = InstrumentSearchParameters(
            search,
            statisticYear,
            statisticMonth,
            statisticType,
            federalEntity,
            municipality,
            agency,
            orders,
            limit,
            offset
        )
    }

    private suspend fun fetchInstruments(parameters: InstrumentSearchParameters) {
        withContext(dispatcher) {
            val (search, statisticYear, statisticMonth, statisticType, federalEntity, municipality, agency, orders, limit, offset) = parameters
            instruments = try {
                val query = SearchInstrumentsByTermQuery(
                    federalEntity?.id,
                    municipality?.id,
                    agency?.id,
                    statisticType?.id,
                    statisticYear,
                    statisticMonth?.first,
                    search,
                    orders.toTypedArray(),
                    limit,
                    offset
                )
                queryBus.ask(query).getOrElse { InstrumentsResponse(emptyList(), 0, null, null) }
            } catch (_: QueryNotRegisteredError) {
                InstrumentsResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun updateInstrumentAsSavedInSIRESO(instrumentId: String) {
        screenModelScope.launch(dispatcher) {
            commandBus.dispatch(UpdateInstrumentAsSavedCommand(instrumentId))
            fetchInstruments(_searchParameters.value)
        }
    }

    fun updateInstrumentAsNotSavedInSIRESO(instrumentId: String) {
        screenModelScope.launch(dispatcher) {
            commandBus.dispatch(UpdateInstrumentAsNotSavedCommand(instrumentId))
            fetchInstruments(_searchParameters.value)
        }
    }

    fun copyInstrumentToClipboard(instrumentId: String) {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchInstrumentByIdQuery(instrumentId)
                queryBus.ask(query).onRight { instrument ->
                    val file = File(tempDirectory).resolve("${instrument.filename}.pdf").apply {
                        parentFile.mkdirs()
                        writeBytes(instrument.instrumentFile.content)
                    }
                    clipboard.setContents(StringSelection(file.absolutePath), null)

                    val notification = Notification(
                        getString(Res.string.inst_copy_notification_title),
                        getString(Res.string.inst_copy_notification_message, file.absolutePath)
                    )
                    trayState.sendNotification(notification)
                }
            } catch (_: QueryNotRegisteredError) {
            }
        }
    }

    fun deleteInstrument(instrumentId: String) {
        screenModelScope.launch(dispatcher) {
            commandBus.dispatch(DeleteInstrumentCommand(instrumentId))
            fetchInstruments(_searchParameters.value)
        }
    }

    fun saveTemplate(file: File) {

    }

    fun importInstruments(file: File) {

    }

    fun importInstrumentsFromV1(file: File) {
        screenModelScope.launch(dispatcher) {
            val input = V1AccdbInstrumentImportInput(file.toPath())
            val command = ImportInstrumentsFromV1Command(input, true)

            importState = commandBus.dispatch(command)
                .fold(
                    ifRight = { warnings ->
                        InstrumentImportState.Success(warnings.map { it.error })
                    },
                    ifLeft = {
                        InstrumentImportState.Error(it)
                    }
                )
        }
    }
}
