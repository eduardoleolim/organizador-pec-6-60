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

package org.eduardoleolim.organizadorpec660.municipality.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.application.delete.DeleteMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.application.searchById.SearchMunicipalityByIdQuery
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.update.UpdateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.data.EmptyMunicipalityDataException
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

@OptIn(FlowPreview::class)
class MunicipalityScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var screenState by mutableStateOf(MunicipalityScreenState())
        private set

    private val _searchParameters = MutableStateFlow(MunicipalitySearchParameters())

    val searchParameters: StateFlow<MunicipalitySearchParameters> = _searchParameters

    var municipalities by mutableStateOf(MunicipalitiesResponse(emptyList(), 0, null, null))
        private set

    var federalEntities by mutableStateOf(emptyList<FederalEntityResponse>())
        private set

    var formState by mutableStateOf<MunicipalityFormState>(MunicipalityFormState.Idle)
        private set

    var deleteState by mutableStateOf<MunicipalityDeleteState>(MunicipalityDeleteState.Idle)
        private set

    var municipality by mutableStateOf(MunicipalityFormData())

    init {
        screenModelScope.launch(dispatcher) {
            searchParameters
                .debounce(500)
                .collectLatest {
                    fetchMunicipalities(it)
                }
        }
    }

    fun showFormModal(municipality: MunicipalityResponse?) {
        screenState = screenState.copy(
            selectedMunicipality = municipality,
            showFormModal = true,
            showDeleteModal = false
        )
    }

    fun showDeleteModal(municipality: MunicipalityResponse) {
        screenState = screenState.copy(
            selectedMunicipality = municipality,
            showFormModal = false,
            showDeleteModal = true
        )
    }

    fun searchMunicipality(municipalityId: String?) {
        screenModelScope.launch(dispatcher) {
            municipality = if (municipalityId == null) {
                MunicipalityFormData()
            } else {
                val municipality = queryBus.ask<MunicipalityResponse>(SearchMunicipalityByIdQuery(municipalityId))

                MunicipalityFormData(
                    municipality.id,
                    municipality.name,
                    municipality.keyCode,
                    municipality.federalEntity
                )
            }
        }
    }

    fun updateMunicipalityName(name: String) {
        municipality = municipality.copy(name = name)
    }

    fun updateMunicipalityKeyCode(keyCode: String) {
        municipality = municipality.copy(keyCode = keyCode)
    }

    fun updateMunicipalityFederalEntity(federalEntity: FederalEntityResponse) {
        municipality = municipality.copy(federalEntity = federalEntity)
    }

    fun resetScreen() {
        screenModelScope.launch(dispatcher) {
            screenState = MunicipalityScreenState()
            val limit = screenState.tableState.pageSize
            val offset = screenState.tableState.pageIndex * limit
            _searchParameters.value = MunicipalitySearchParameters(limit = limit, offset = offset)
            fetchAllFederalEntities()
            fetchMunicipalities(searchParameters.value)
        }
    }

    fun resetFormModal() {
        formState = MunicipalityFormState.Idle
    }

    fun resetDeleteModal() {
        deleteState = MunicipalityDeleteState.Idle
    }

    fun searchMunicipalities(
        search: String = searchParameters.value.search,
        federalEntity: FederalEntityResponse? = searchParameters.value.federalEntity,
        orders: List<HashMap<String, String>> = searchParameters.value.orders,
        limit: Int? = searchParameters.value.limit,
        offset: Int? = searchParameters.value.offset
    ) {
        _searchParameters.value = MunicipalitySearchParameters(search, federalEntity, orders, limit, offset)
    }

    private suspend fun fetchMunicipalities(parameters: MunicipalitySearchParameters) {
        withContext(dispatcher) {
            val (search, federalEntity, orders, limit, offset) = parameters
            screenModelScope.launch(dispatcher) {
                try {
                    val query =
                        SearchMunicipalitiesByTermQuery(federalEntity?.id, search, orders.toTypedArray(), limit, offset)
                    municipalities = queryBus.ask(query)
                } catch (e: Exception) {
                    municipalities = MunicipalitiesResponse(emptyList(), 0, null, null)
                }
            }
        }
    }

    fun searchAllFederalEntities() {
        screenModelScope.launch(dispatcher) {
            fetchAllFederalEntities()
        }
    }

    private suspend fun fetchAllFederalEntities() {
        withContext(dispatcher) {
            try {
                val query = SearchFederalEntitiesByTermQuery()
                federalEntities = queryBus.ask<FederalEntitiesResponse>(query).federalEntities
            } catch (e: Exception) {
                federalEntities = emptyList()
            }
        }
    }

    fun saveMunicipality() {
        screenModelScope.launch(dispatcher) {
            val (id, name, keyCode, federalEntity) = municipality
            val isFederalEntityUnselected = federalEntity == null
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            formState = MunicipalityFormState.InProgress
            delay(500)

            if (isFederalEntityUnselected || isKeyCodeEmpty || isNameEmpty) {
                formState = MunicipalityFormState.Error(
                    EmptyMunicipalityDataException(
                        isFederalEntityUnselected,
                        isKeyCodeEmpty,
                        isNameEmpty
                    )
                )
                return@launch
            }

            if (id == null) {
                createMunicipality(keyCode, name, federalEntity!!.id)
            } else {
                updateMunicipality(id, keyCode, name, federalEntity!!.id)
            }
        }
    }

    private suspend fun createMunicipality(keyCode: String, name: String, federalEntityId: String) {
        withContext(dispatcher) {
            formState = try {
                commandBus.dispatch(CreateMunicipalityCommand(keyCode, name, federalEntityId)).fold(
                    ifRight = {
                        MunicipalityFormState.SuccessCreate
                    },
                    ifLeft = {
                        MunicipalityFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                MunicipalityFormState.Error(e.cause!!)
            }
        }
    }

    private suspend fun updateMunicipality(
        municipalityId: String,
        keyCode: String,
        name: String,
        federalEntityId: String
    ) {
        withContext(dispatcher) {
            formState = try {
                commandBus.dispatch(UpdateMunicipalityCommand(municipalityId, keyCode, name, federalEntityId))
                    .fold(
                        ifRight = {
                            MunicipalityFormState.SuccessEdit
                        },
                        ifLeft = {
                            MunicipalityFormState.Error(it)
                        }
                    )
            } catch (e: Exception) {
                MunicipalityFormState.Error(e.cause!!)
            }
        }
    }

    fun deleteMunicipality(municipalityId: String) {
        screenModelScope.launch(dispatcher) {
            deleteState = MunicipalityDeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteMunicipalityCommand(municipalityId))
                    .fold(
                        ifRight = {
                            deleteState = MunicipalityDeleteState.Success
                        },
                        ifLeft = {
                            deleteState = MunicipalityDeleteState.Error(it)
                        }
                    )

            } catch (e: Exception) {
                deleteState = MunicipalityDeleteState.Error(e.cause!!)
            }
        }
    }
}
