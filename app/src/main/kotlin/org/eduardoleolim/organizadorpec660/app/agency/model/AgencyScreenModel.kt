package org.eduardoleolim.organizadorpec660.app.agency.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.agency.data.EmptyAgencyDataException
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.ag_delete_error_default
import org.eduardoleolim.organizadorpec660.app.generated.resources.ag_delete_error_has_instruments
import org.eduardoleolim.organizadorpec660.app.generated.resources.ag_delete_error_not_found
import org.eduardoleolim.organizadorpec660.core.agency.application.AgenciesResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.create.CreateAgencyCommand
import org.eduardoleolim.organizadorpec660.core.agency.application.delete.DeleteAgencyCommand
import org.eduardoleolim.organizadorpec660.core.agency.application.searchByTerm.SearchAgenciesByTermQuery
import org.eduardoleolim.organizadorpec660.core.agency.application.update.UpdateAgencyCommand
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyHasInstrumentsError
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorpec660.core.agency.domain.CanNotDeleteAgencyError
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
import org.jetbrains.compose.resources.getString

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
    data class Error(val message: String) : DeleteState()
}

class AgencyScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
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
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchAgenciesByTermQuery(search, orders, limit, offset)
                agencies = queryBus.ask(query)
            } catch (e: Exception) {
                agencies = AgenciesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun searchMunicipalities(federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            municipalities = federalEntityId?.let {
                try {
                    val query = SearchMunicipalitiesByTermQuery(federalEntityId)
                    queryBus.ask<MunicipalitiesResponse>(query).municipalities
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()
        }
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

    fun searchAllStatisticTypes() {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchStatisticTypesByTermQuery()
                statisticTypes = queryBus.ask<StatisticTypesResponse>(query).statisticTypes
            } catch (e: Exception) {
                statisticTypes = emptyList()
            }
        }
    }

    fun createAgency(name: String, consecutive: String, municipalityId: String?, statisticTypesId: List<String>) {
        screenModelScope.launch(dispatcher) {
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
            } else {
                try {
                    val command = CreateAgencyCommand(name, consecutive, municipalityId!!, statisticTypesId)
                    commandBus.dispatch(command).fold(
                        ifRight = {
                            formState = FormState.SuccessCreate
                        },
                        ifLeft = {
                            formState = FormState.Error(it)
                        }
                    )
                } catch (e: Exception) {
                    formState = FormState.Error(e.cause!!)
                }
            }
        }
    }

    fun updateAgency(
        agencyId: String,
        name: String,
        consecutive: String,
        municipalityId: String?,
        statisticTypesId: List<String>
    ) {
        screenModelScope.launch(dispatcher) {
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
            } else {
                try {
                    val command = UpdateAgencyCommand(agencyId, name, consecutive, municipalityId!!, statisticTypesId)
                    commandBus.dispatch(command).fold(
                        ifRight = {
                            formState = FormState.SuccessEdit
                        },
                        ifLeft = {
                            formState = FormState.Error(it)
                        }
                    )
                } catch (e: Exception) {
                    formState = FormState.Error(e.cause!!)
                }
            }
        }
    }

    fun deleteAgency(agencyId: String) {
        screenModelScope.launch(dispatcher) {
            deleteState = DeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteAgencyCommand(agencyId)).foldAsync(
                    ifRight = {
                        deleteState = DeleteState.Success
                    },
                    ifLeft = { error ->
                        val message = when (error) {
                            is AgencyNotFoundError -> getString(Res.string.ag_delete_error_not_found)

                            is AgencyHasInstrumentsError -> getString(Res.string.ag_delete_error_has_instruments)

                            is CanNotDeleteAgencyError -> getString(Res.string.ag_delete_error_default)

                            else -> getString(Res.string.ag_delete_error_default)
                        }

                        deleteState = DeleteState.Error(message)
                    }
                )
            } catch (e: Exception) {
                deleteState = DeleteState.Error(getString(Res.string.ag_delete_error_default))
            }
        }
    }
}
