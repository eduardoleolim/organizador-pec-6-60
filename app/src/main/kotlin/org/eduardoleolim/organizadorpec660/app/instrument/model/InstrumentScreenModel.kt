package org.eduardoleolim.organizadorpec660.app.instrument.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.MunicipalityAgenciesResponse
import org.eduardoleolim.organizadorpec660.agency.application.searchByMunicipalityId.SearchAgenciesByMunicipalityIdQuery
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.inst_copy_notification_message
import org.eduardoleolim.organizadorpec660.app.generated.resources.inst_copy_notification_title
import org.eduardoleolim.organizadorpec660.app.shared.router.HomeProvider
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.instrument.application.DetailedInstrumentResponse
import org.eduardoleolim.organizadorpec660.core.instrument.application.InstrumentsResponse
import org.eduardoleolim.organizadorpec660.core.instrument.application.delete.DeleteInstrumentCommand
import org.eduardoleolim.organizadorpec660.core.instrument.application.save.UpdateInstrumentAsNotSavedCommand
import org.eduardoleolim.organizadorpec660.core.instrument.application.save.UpdateInstrumentAsSavedCommand
import org.eduardoleolim.organizadorpec660.core.instrument.application.searchById.SearchInstrumentByIdQuery
import org.eduardoleolim.organizadorpec660.core.instrument.application.searchByTerm.SearchInstrumentsByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
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

class InstrumentScreenModel(
    private val navigator: Navigator,
    private val trayState: TrayState,
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val tempDirectory: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    private val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard

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

    fun searchAllFederalEntities() {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchFederalEntitiesByTermQuery()
                federalEntities = queryBus.ask<FederalEntitiesResponse>(query).federalEntities
            } catch (e: Exception) {
                federalEntities = emptyList()
            }
        }
    }

    fun searchMunicipalities(federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            municipalities = federalEntityId?.let { id ->
                try {
                    val query = SearchMunicipalitiesByTermQuery(id)
                    queryBus.ask<MunicipalitiesResponse>(query).municipalities
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()
        }
    }

    fun searchAgencies(municipalityId: String?) {
        screenModelScope.launch(dispatcher) {
            agencies = municipalityId?.let {
                try {
                    val query = SearchAgenciesByMunicipalityIdQuery(municipalityId)
                    queryBus.ask<MunicipalityAgenciesResponse>(query).agencies
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()
        }
    }

    fun searchAllStatisticTypes() {
        statisticTypes = try {
            queryBus.ask<StatisticTypesResponse>(SearchStatisticTypesByTermQuery()).statisticTypes
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun navigateToSaveInstrumentView(instrumentId: String?) {
        navigator.push(ScreenRegistry.get(HomeProvider.SaveInstrumentScreen(instrumentId)))
    }

    fun navigateToShowInstrumentDetailsView(instrumentId: String) {
        navigator.push(ScreenRegistry.get(HomeProvider.ShowInstrumentDetailsScreen(instrumentId)))
    }

    fun searchInstruments(
        search: String? = null,
        federalEntityId: String? = null,
        municipalityId: String? = null,
        agencyId: String? = null,
        statisticTypeId: String? = null,
        statisticYear: Int? = null,
        statisticMonth: Int? = null,
        orders: List<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
    ) {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchInstrumentsByTermQuery(
                    federalEntityId,
                    municipalityId,
                    agencyId,
                    statisticTypeId,
                    statisticYear,
                    statisticMonth,
                    search,
                    orders?.toTypedArray(),
                    limit,
                    offset
                )
                instruments = queryBus.ask(query)
            } catch (e: Exception) {
                instruments = InstrumentsResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun updateInstrumentAsSavedInSIRESO(instrumentId: String) {
        commandBus.dispatch(UpdateInstrumentAsSavedCommand(instrumentId))
    }

    fun updateInstrumentAsNotSavedInSIRESO(instrumentId: String) {
        commandBus.dispatch(UpdateInstrumentAsNotSavedCommand(instrumentId))
    }

    fun copyInstrumentToClipboard(instrumentId: String) {
        screenModelScope.launch(dispatcher) {
            val instrument = queryBus.ask<DetailedInstrumentResponse>(SearchInstrumentByIdQuery(instrumentId))
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
    }

    fun deleteInstrument(instrumentId: String) {
        screenModelScope.launch(dispatcher) {
            commandBus.dispatch(DeleteInstrumentCommand(instrumentId))
        }
    }
}
