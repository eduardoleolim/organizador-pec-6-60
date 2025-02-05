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

package org.eduardoleolim.organizadorpec660.agency.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import arrow.core.flatMap
import arrow.core.getOrElse
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.eduardoleolim.organizadorpec660.agency.application.AgenciesResponse
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.create.CreateAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.delete.DeleteAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.searchById.SearchAgencyByIdQuery
import org.eduardoleolim.organizadorpec660.agency.application.searchByTerm.SearchAgenciesByTermQuery
import org.eduardoleolim.organizadorpec660.agency.application.update.UpdateAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.data.EmptyAgencyDataException
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyHasInstrumentsError
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorpec660.agency.domain.CanNotDeleteAgencyError
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchById.SearchFederalEntityByIdQuery
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandNotRegisteredError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryNotRegisteredError
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.ag_delete_error_default
import org.eduardoleolim.organizadorpec660.shared.resources.ag_delete_error_has_instruments
import org.eduardoleolim.organizadorpec660.shared.resources.ag_delete_error_not_found
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.jetbrains.compose.resources.getString

@OptIn(FlowPreview::class)
class AgencyScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var screenState by mutableStateOf(AgencyScreenState())
        private set

    private val _searchParameters = MutableStateFlow(AgencySearchParameters())

    val searchParameters: StateFlow<AgencySearchParameters> = _searchParameters

    var agencies by mutableStateOf(AgenciesResponse(emptyList(), 0, null, null))
        private set

    var municipalities by mutableStateOf(emptyList<MunicipalityResponse>())
        private set

    var federalEntities by mutableStateOf(emptyList<FederalEntityResponse>())
        private set

    var statisticTypes by mutableStateOf(emptyList<StatisticTypeResponse>())
        private set

    var formState by mutableStateOf<AgencyFormState>(AgencyFormState.Idle)
        private set

    var deleteState by mutableStateOf<AgencyDeleteState>(AgencyDeleteState.Idle)
        private set

    var agency by mutableStateOf(AgencyFormData())
        private set

    init {
        screenModelScope.launch(dispatcher) {
            _searchParameters
                .debounce(500)
                .collectLatest {
                    fetchAgencies(it)
                }
        }
    }

    fun showFormModal(agency: AgencyResponse?) {
        screenState = screenState.copy(
            selectedAgency = agency,
            showFormModal = true,
            showDeleteModal = false
        )
    }

    fun showDeleteModal(agency: AgencyResponse) {
        screenState = screenState.copy(
            selectedAgency = agency,
            showFormModal = false,
            showDeleteModal = true
        )
    }

    fun searchAgency(agencyId: String?) {
        screenModelScope.launch(dispatcher) {
            agency = try {
                if (agencyId == null) {
                    AgencyFormData()
                } else {
                    val agencyQuery = SearchAgencyByIdQuery(agencyId)
                    queryBus.ask(agencyQuery).flatMap { agencyResponse ->
                        val federalEntityQuery =
                            SearchFederalEntityByIdQuery(agencyResponse.municipality.federalEntityId)
                        queryBus.ask(federalEntityQuery).map { federalEntityResponse ->
                            AgencyFormData(
                                agencyResponse.id,
                                agencyResponse.name,
                                agencyResponse.consecutive,
                                federalEntityResponse,
                                agencyResponse.municipality,
                                agencyResponse.statisticTypes
                            )
                        }
                    }.getOrElse {
                        AgencyFormData()
                    }
                }
            } catch (_: QueryNotRegisteredError) {
                AgencyFormData()
            }
        }
    }

    fun updateAgencyName(name: String) {
        agency = agency.copy(name = name)
    }

    fun updateAgencyConsecutive(consecutive: String) {
        agency = agency.copy(consecutive = consecutive)
    }

    fun updateAgencyFederalEntity(federalEntity: FederalEntityResponse) {
        if (agency.federalEntity?.id != federalEntity.id) {
            agency = agency.copy(federalEntity = federalEntity, municipality = null)
        }
    }

    fun updateAgencyMunicipality(municipality: MunicipalityResponse) {
        val simpleMunicipality = SimpleMunicipalityResponse(
            municipality.id,
            municipality.name,
            municipality.keyCode,
            municipality.federalEntity.id,
            municipality.createdAt,
            municipality.updatedAt
        )
        agency = agency.copy(municipality = simpleMunicipality)
    }

    fun addAgencyStatisticType(statisticType: StatisticTypeResponse) {
        val statisticTypes = agency.statisticTypes.toMutableList().apply {
            if (none { it.id == statisticType.id }) {
                add(statisticType)
            }
        }
        agency = agency.copy(statisticTypes = statisticTypes)
    }

    fun removeAgencyStatisticType(statisticType: StatisticTypeResponse) {
        val statisticTypes = agency.statisticTypes.toMutableList().apply { removeIf { it.id == statisticType.id } }
        agency = agency.copy(statisticTypes = statisticTypes)
    }

    fun resetScreen() {
        screenModelScope.launch(dispatcher) {
            screenState = AgencyScreenState()
            val limit = screenState.tableState.pageSize
            val offset = screenState.tableState.pageIndex * limit
            _searchParameters.value = AgencySearchParameters(limit = limit, offset = offset)
            fetchAgencies(searchParameters.value)
        }
    }

    fun resetFormModal() {
        formState = AgencyFormState.Idle
        agency = AgencyFormData()
    }

    fun resetDeleteModal() {
        deleteState = AgencyDeleteState.Idle
    }

    fun searchAgencies(
        search: String = searchParameters.value.search,
        orders: List<HashMap<String, String>> = searchParameters.value.orders,
        limit: Int? = searchParameters.value.limit,
        offset: Int? = searchParameters.value.offset,
    ) {
        _searchParameters.value = AgencySearchParameters(search, orders, limit, offset)
    }

    private suspend fun fetchAgencies(parameters: AgencySearchParameters) {
        withContext(dispatcher) {
            val (search, orders, limit, offset) = parameters
            try {
                val query = SearchAgenciesByTermQuery(search, orders.toTypedArray(), limit, offset)
                agencies = queryBus.ask(query).getOrElse { AgenciesResponse(emptyList(), 0, null, null) }
            } catch (_: QueryNotRegisteredError) {
                agencies = AgenciesResponse(emptyList(), 0, null, null)
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

    fun searchAllStatisticTypes() {
        screenModelScope.launch(dispatcher) {
            statisticTypes = try {
                val query = SearchStatisticTypesByTermQuery()
                queryBus.ask(query).map { it.statisticTypes }.getOrElse { emptyList() }
            } catch (_: QueryNotRegisteredError) {
                emptyList()
            }
        }
    }

    fun saveAgency() {
        screenModelScope.launch(dispatcher) {
            val (id, name, consecutive, _, municipality, statisticTypes) = agency
            val isNameEmpty = name.isEmpty()
            val isConsecutiveEmpty = name.isEmpty()
            val isMunicipalityUnselected = municipality == null
            val isStatisticTypesEmpty = statisticTypes.isEmpty()

            formState = AgencyFormState.InProgress
            delay(500)

            if (isNameEmpty || isConsecutiveEmpty || isMunicipalityUnselected || isStatisticTypesEmpty) {
                formState = AgencyFormState.Error(
                    EmptyAgencyDataException(
                        isNameEmpty,
                        isConsecutiveEmpty,
                        isMunicipalityUnselected,
                        isStatisticTypesEmpty
                    )
                )

                return@launch
            }

            if (agency.id == null) {
                createAgency(name, consecutive, municipality, statisticTypes)
            } else {
                updateAgency(id!!, name, consecutive, municipality, statisticTypes)
            }
        }
    }

    private fun createAgency(
        name: String,
        consecutive: String,
        municipality: SimpleMunicipalityResponse,
        statisticTypes: List<StatisticTypeResponse>
    ) {
        formState = try {
            val statisticTypeIds = statisticTypes.map { it.id }
            val command = CreateAgencyCommand(name, consecutive, municipality.id, statisticTypeIds)
            commandBus.dispatch(command).fold(
                ifRight = {
                    AgencyFormState.SuccessCreate
                },
                ifLeft = {
                    AgencyFormState.Error(it)
                }
            )
        } catch (e: CommandNotRegisteredError) {
            AgencyFormState.Error(e)
        }
    }

    private fun updateAgency(
        id: String,
        name: String,
        consecutive: String,
        municipality: SimpleMunicipalityResponse,
        statisticTypes: List<StatisticTypeResponse>
    ) {
        formState = try {
            val statisticTypeIds = statisticTypes.map { it.id }
            val command = UpdateAgencyCommand(id, name, consecutive, municipality.id, statisticTypeIds)
            commandBus.dispatch(command).fold(
                ifRight = {
                    AgencyFormState.SuccessEdit
                },
                ifLeft = {
                    AgencyFormState.Error(it)
                }
            )
        } catch (e: CommandNotRegisteredError) {
            AgencyFormState.Error(e)
        }
    }

    fun deleteAgency(agencyId: String) {
        screenModelScope.launch(dispatcher) {
            deleteState = AgencyDeleteState.InProgress
            delay(500)

            deleteState = try {
                val command = DeleteAgencyCommand(agencyId)
                commandBus.dispatch(command).fold(
                    ifRight = {
                        AgencyDeleteState.Success
                    },
                    ifLeft = { error ->
                        val message = when (error) {
                            is AgencyNotFoundError -> getString(Res.string.ag_delete_error_not_found)
                            is AgencyHasInstrumentsError -> getString(Res.string.ag_delete_error_has_instruments)
                            is CanNotDeleteAgencyError -> getString(Res.string.ag_delete_error_default)
                            else -> getString(Res.string.ag_delete_error_default)
                        }

                        AgencyDeleteState.Error(message)
                    }
                )
            } catch (_: CommandNotRegisteredError) {
                AgencyDeleteState.Error(getString(Res.string.ag_delete_error_default))
            }
        }
    }
}
