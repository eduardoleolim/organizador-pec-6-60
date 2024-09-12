package org.eduardoleolim.organizadorpec660.municipality.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.municipality.data.EmptyMunicipalityDataException
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.application.delete.DeleteMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.update.UpdateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

sealed class MunicipalityFormState {
    data object Idle : MunicipalityFormState()
    data object InProgress : MunicipalityFormState()
    data object SuccessCreate : MunicipalityFormState()
    data object SuccessEdit : MunicipalityFormState()
    data class Error(val error: Throwable) : MunicipalityFormState()
}

sealed class MunicipalityDeleteState {
    data object Idle : MunicipalityDeleteState()
    data object InProgress : MunicipalityDeleteState()
    data object Success : MunicipalityDeleteState()
    data class Error(val error: Throwable) : MunicipalityDeleteState()
}

class MunicipalityScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var municipalities by mutableStateOf(MunicipalitiesResponse(emptyList(), 0, null, null))
        private set

    var federalEntities by mutableStateOf(emptyList<FederalEntityResponse>())
        private set

    var formState by mutableStateOf<MunicipalityFormState>(MunicipalityFormState.Idle)
        private set

    var deleteState by mutableStateOf<MunicipalityDeleteState>(MunicipalityDeleteState.Idle)
        private set

    fun resetForm() {
        formState = MunicipalityFormState.Idle
    }

    fun resetDeleteModal() {
        deleteState = MunicipalityDeleteState.Idle
    }

    fun searchMunicipalities(
        search: String? = null,
        federalEntityId: String? = null,
        orders: List<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) {
        screenModelScope.launch(dispatcher) {
            try {
                val query =
                    SearchMunicipalitiesByTermQuery(federalEntityId, search, orders?.toTypedArray(), limit, offset)
                municipalities = queryBus.ask(query)
            } catch (e: Exception) {
                municipalities = MunicipalitiesResponse(emptyList(), 0, null, null)
            }
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

    fun createMunicipality(keyCode: String, name: String, federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            formState = MunicipalityFormState.InProgress
            delay(500)

            val isFederalEntityEmpty = federalEntityId.isNullOrBlank()
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isFederalEntityEmpty || isKeyCodeEmpty || isNameEmpty) {
                formState =
                    MunicipalityFormState.Error(
                        EmptyMunicipalityDataException(
                            isFederalEntityEmpty,
                            isKeyCodeEmpty,
                            isNameEmpty
                        )
                    )
                return@launch
            }

            try {
                commandBus.dispatch(CreateMunicipalityCommand(keyCode, name, federalEntityId!!)).fold(
                    ifRight = {
                        formState = MunicipalityFormState.SuccessCreate
                    },
                    ifLeft = {
                        formState = MunicipalityFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                formState = MunicipalityFormState.Error(e.cause!!)
            }
        }
    }

    fun editMunicipality(municipalityId: String, keyCode: String, name: String, federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            formState = MunicipalityFormState.InProgress
            delay(500)

            val isFederalEntityEmpty = federalEntityId.isNullOrBlank()
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isFederalEntityEmpty || isKeyCodeEmpty || isNameEmpty) {
                formState =
                    MunicipalityFormState.Error(
                        EmptyMunicipalityDataException(
                            isFederalEntityEmpty,
                            isKeyCodeEmpty,
                            isNameEmpty
                        )
                    )
                return@launch
            }

            try {
                commandBus.dispatch(UpdateMunicipalityCommand(municipalityId, keyCode, name, federalEntityId!!)).fold(
                    ifRight = {
                        formState = MunicipalityFormState.SuccessEdit
                    },
                    ifLeft = {
                        formState = MunicipalityFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                formState = MunicipalityFormState.Error(e.cause!!)
            }
        }
    }

    fun deleteMunicipality(municipalityId: String) {
        screenModelScope.launch(dispatcher) {
            deleteState = MunicipalityDeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteMunicipalityCommand(municipalityId)).fold(
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
