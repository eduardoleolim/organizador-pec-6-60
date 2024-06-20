package org.eduardoleolim.organizadorpec660.app.agency.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.agency.data.EmptyAgencyDataException
import org.eduardoleolim.organizadorpec660.core.agency.application.AgenciesResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.create.CreateAgencyCommand
import org.eduardoleolim.organizadorpec660.core.agency.application.searchByTerm.SearchAgenciesByTermQuery
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery

sealed class FormState {
    data object Idle : FormState()
    data object InProgress : FormState()
    data object SuccessCreate : FormState()
    data object SuccessEdit : FormState()
    data class Error(val error: Throwable) : FormState()
}

sealed class DeleteState {
    data object Idle : DeleteState()
    data object InProgress : DeleteState()
    data object Success : DeleteState()
    data class Error(val error: Throwable) : DeleteState()
}

class AgencyScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    var agencies by mutableStateOf(AgenciesResponse(emptyList(), 0, null, null))
        private set

    var municipalities by mutableStateOf(emptyList<MunicipalityResponse>())
        private set

    var federalEntities by mutableStateOf(emptyList<FederalEntityResponse>())
        private set

    var statisticTypes by mutableStateOf(emptyList<StatisticTypeResponse>())
        private set

    var formState by mutableStateOf<FormState>(FormState.Idle)
        private set

    var deleteState by mutableStateOf<DeleteState>(DeleteState.Idle)
        private set

    fun resetForm() {
        formState = FormState.Idle
    }

    fun resetDeleteModal() {
        deleteState = DeleteState.Idle
    }

    fun searchAgencies(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchAgenciesByTermQuery(search, orders, limit, offset)
                agencies = queryBus.ask(query)
            } catch (e: Exception) {
                agencies = AgenciesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun searchMunicipalities(federalEntityId: String?) {
        screenModelScope.launch(Dispatchers.IO) {
            if (federalEntityId == null) {
                municipalities = emptyList()

                return@launch
            }

            try {
                val query = SearchMunicipalitiesByTermQuery(federalEntityId)
                municipalities = queryBus.ask<MunicipalitiesResponse>(query).municipalities
            } catch (e: Exception) {
                municipalities = emptyList()
            }
        }
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

    fun searchAllStatisticTypes() {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchStatisticTypesByTermQuery()
                statisticTypes = queryBus.ask<StatisticTypesResponse>(query).statisticTypes
            } catch (e: Exception) {
                statisticTypes = emptyList()
            }
        }
    }

    fun createAgency(name: String, consecutive: String, municipalityId: String?, statisticTypesId: List<String>) {
        screenModelScope.launch(Dispatchers.IO) {
            formState = FormState.InProgress
            delay(500)

            val isNameEmpty = name.isEmpty()
            val isConsecutiveEmpty = name.isEmpty()
            val isMunicipalityEmpty = municipalityId.isNullOrEmpty()
            val isStatisticTypesEmpty = statisticTypesId.isEmpty()

            if (isNameEmpty || isConsecutiveEmpty || isMunicipalityEmpty || isStatisticTypesEmpty) {
                formState = FormState.Error(
                    EmptyAgencyDataException(
                        isNameEmpty,
                        isConsecutiveEmpty,
                        isMunicipalityEmpty,
                        isStatisticTypesEmpty
                    )
                )

                return@launch
            }

            try {
                commandBus.dispatch(CreateAgencyCommand(name, consecutive, municipalityId!!, statisticTypesId))
                formState = FormState.SuccessCreate
            } catch (e: Exception) {
                formState = FormState.Error(e.cause!!)
            }
        }
    }
}
