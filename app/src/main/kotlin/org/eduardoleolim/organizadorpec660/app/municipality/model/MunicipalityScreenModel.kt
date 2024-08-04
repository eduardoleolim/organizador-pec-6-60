package org.eduardoleolim.organizadorpec660.app.municipality.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.municipality.data.EmptyMunicipalityDataException
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.delete.DeleteMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.municipality.application.update.UpdateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

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

class MunicipalityScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var municipalities by mutableStateOf(MunicipalitiesResponse(emptyList(), 0, null, null))
        private set

    var federalEntities by mutableStateOf(emptyList<FederalEntityResponse>())
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

    fun searchMunicipalities(
        search: String? = null,
        federalEntityId: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchMunicipalitiesByTermQuery(federalEntityId, search, orders, limit, offset)
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
            formState = FormState.InProgress
            delay(500)

            val isFederalEntityEmpty = federalEntityId.isNullOrBlank()
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isFederalEntityEmpty || isKeyCodeEmpty || isNameEmpty) {
                formState =
                    FormState.Error(EmptyMunicipalityDataException(isFederalEntityEmpty, isKeyCodeEmpty, isNameEmpty))
                return@launch
            }

            try {
                commandBus.dispatch(CreateMunicipalityCommand(keyCode, name, federalEntityId!!)).fold(
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

    fun editMunicipality(municipalityId: String, keyCode: String, name: String, federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            formState = FormState.InProgress
            delay(500)

            val isFederalEntityEmpty = federalEntityId.isNullOrBlank()
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isFederalEntityEmpty || isKeyCodeEmpty || isNameEmpty) {
                formState =
                    FormState.Error(EmptyMunicipalityDataException(isFederalEntityEmpty, isKeyCodeEmpty, isNameEmpty))
                return@launch
            }

            try {
                commandBus.dispatch(UpdateMunicipalityCommand(municipalityId, keyCode, name, federalEntityId!!)).fold(
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

    fun deleteMunicipality(municipalityId: String) {
        screenModelScope.launch(dispatcher) {
            deleteState = DeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteMunicipalityCommand(municipalityId)).fold(
                    ifRight = {
                        deleteState = DeleteState.Success
                    },
                    ifLeft = {
                        deleteState = DeleteState.Error(it)
                    }
                )

            } catch (e: Exception) {
                deleteState = DeleteState.Error(e.cause!!)
            }
        }
    }
}
