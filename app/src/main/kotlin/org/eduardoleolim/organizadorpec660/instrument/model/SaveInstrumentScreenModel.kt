package org.eduardoleolim.organizadorpec660.instrument.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.MunicipalityAgenciesResponse
import org.eduardoleolim.organizadorpec660.agency.application.searchByMunicipalityId.SearchAgenciesByMunicipalityIdQuery
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.instrument.application.DetailedInstrumentResponse
import org.eduardoleolim.organizadorpec660.instrument.application.create.CreateInstrumentCommand
import org.eduardoleolim.organizadorpec660.instrument.application.searchById.SearchInstrumentByIdQuery
import org.eduardoleolim.organizadorpec660.instrument.application.update.UpdateInstrumentCommand
import org.eduardoleolim.organizadorpec660.instrument.views.InstrumentScreen
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import java.io.File

sealed class InstrumentFormState {
    data object Idle : InstrumentFormState()
    data object InProgress : InstrumentFormState()
    data object SuccessCreate : InstrumentFormState()
    data object SuccessEdit : InstrumentFormState()
    data class Error(val error: Throwable) : InstrumentFormState()
}

class SaveInstrumentScreenModel(
    private val navigator: Navigator,
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
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

    fun goBackToInstrumentView() {
        navigator.popUntil<InstrumentScreen, Screen>()
    }

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

    fun searchStatisticTypes(agencyId: String?) {
        statisticTypes = try {
            agencies.first { it.id == agencyId }.statisticTypes
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun searchInstrument(instrumentId: String): DetailedInstrumentResponse {
        return queryBus.ask(SearchInstrumentByIdQuery(instrumentId))
    }

    fun saveInstrument(
        year: Int?,
        month: Int?,
        municipalityId: String?,
        agencyId: String?,
        statisticTypeId: String?,
        documentPath: String?
    ) {
        screenModelScope.launch(dispatcher) {
            formState = InstrumentFormState.InProgress
            delay(500)

            if (year == null || month == null || municipalityId == null || agencyId == null || statisticTypeId == null || documentPath == null) {
                formState = InstrumentFormState.Error(Exception("Campos Vacíos"))
            } else {
                try {
                    val document = File(documentPath).readBytes()
                    val command =
                        CreateInstrumentCommand(year, month, agencyId, statisticTypeId, municipalityId, document)
                    commandBus.dispatch(command).fold(
                        ifRight = {
                            formState = InstrumentFormState.SuccessCreate
                        },
                        ifLeft = {
                            formState = InstrumentFormState.Error(it)
                        }
                    )
                } catch (e: Exception) {
                    formState = InstrumentFormState.Error(e.cause!!)
                }
            }
        }
    }

    fun editInstrument(
        instrumentId: String,
        year: Int?,
        month: Int?,
        municipalityId: String?,
        agencyId: String?,
        statisticTypeId: String?,
        documentPath: String?
    ) {
        screenModelScope.launch(dispatcher) {
            formState = InstrumentFormState.InProgress
            delay(500)

            if (year == null || month == null || municipalityId == null || agencyId == null || statisticTypeId == null || documentPath == null) {
                formState = InstrumentFormState.Error(Exception("Campos Vacíos"))
            } else {
                try {
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
                            formState = InstrumentFormState.SuccessEdit
                        },
                        ifLeft = {
                            formState = InstrumentFormState.Error(it)
                        }
                    )
                } catch (e: Exception) {
                    formState = InstrumentFormState.Error(e.cause!!)
                }
            }
        }
    }
}
