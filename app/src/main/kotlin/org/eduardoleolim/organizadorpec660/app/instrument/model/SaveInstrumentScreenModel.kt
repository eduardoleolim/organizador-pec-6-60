package org.eduardoleolim.organizadorpec660.app.instrument.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.instrument.views.InstrumentScreen
import org.eduardoleolim.organizadorpec660.core.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.MunicipalityAgenciesResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.searchByMunicipalityId.SearchAgenciesByMunicipalityIdQuery
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.instrument.application.create.CreateInstrumentCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypeResponse
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
    private val commandBus: CommandBus
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
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchFederalEntitiesByTermQuery()
                federalEntities = queryBus.ask<FederalEntitiesResponse>(query).federalEntities
            } catch (e: Exception) {
                federalEntities = emptyList()
            }
        }
    }

    fun searchMunicipalities(federalEntityId: String?) {
        screenModelScope.launch(Dispatchers.IO) {
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
        screenModelScope.launch(Dispatchers.IO) {
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

    fun saveInstrument(
        year: Int?,
        month: Int?,
        municipalityId: String?,
        agencyId: String?,
        statisticTypeId: String?,
        documentPath: String?
    ) {
        screenModelScope.launch(Dispatchers.IO) {
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
        statisticTypeId: String?
    ) {

    }
}