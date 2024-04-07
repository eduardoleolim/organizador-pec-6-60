package org.eduardoleolim.organizadorpec660.app.municipality.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
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

class MunicipalityScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    private var _municipalities = mutableStateOf(MunicipalitiesResponse(emptyList(), 0, null, null))
    val municipalities: State<MunicipalitiesResponse> get() = _municipalities

    private var _federalEntities = mutableStateOf(emptyList<FederalEntityResponse>())
    val federalEntities: State<List<FederalEntityResponse>> get() = _federalEntities

    private var _formState = mutableStateOf<FormState>(FormState.Idle)
    val formState: State<FormState> get() = _formState

    private var _deleteState = mutableStateOf<DeleteState>(DeleteState.Idle)
    val deleteState: State<DeleteState> get() = _deleteState

    fun resetForm() {
        _formState.value = FormState.Idle
    }

    fun resetDeleteModal() {
        _deleteState.value = DeleteState.Idle
    }

    fun searchMunicipalities(
        search: String? = null,
        federalEntityId: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchMunicipalitiesByTermQuery(federalEntityId, search, orders, limit, offset)
                _municipalities.value = queryBus.ask(query)
            } catch (e: Exception) {
                _municipalities.value = MunicipalitiesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun searchAllFederalEntities() {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchFederalEntitiesByTermQuery()
                _federalEntities.value = queryBus.ask<FederalEntitiesResponse>(query).federalEntities
            } catch (e: Exception) {
                _federalEntities.value = emptyList()
            }
        }
    }

    fun createMunicipality(keyCode: String, name: String, federalEntityId: String?) {
        _formState.value = FormState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            val isFederalEntityEmpty = federalEntityId.isNullOrBlank()
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isFederalEntityEmpty || isKeyCodeEmpty || isNameEmpty) {
                _formState.value =
                    FormState.Error(EmptyMunicipalityDataException(isFederalEntityEmpty, isKeyCodeEmpty, isNameEmpty))
                return@launch
            }

            try {
                commandBus.dispatch(CreateMunicipalityCommand(keyCode, name, federalEntityId!!))
                _formState.value = FormState.SuccessCreate
            } catch (e: Exception) {
                _formState.value = FormState.Error(e.cause!!)
            }
        }
    }

    fun editMunicipality(municipalityId: String, keyCode: String, name: String, federalEntityId: String?) {
        _formState.value = FormState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            val isFederalEntityEmpty = federalEntityId.isNullOrBlank()
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isFederalEntityEmpty || isKeyCodeEmpty || isNameEmpty) {
                _formState.value =
                    FormState.Error(EmptyMunicipalityDataException(isFederalEntityEmpty, isKeyCodeEmpty, isNameEmpty))
                return@launch
            }

            try {
                commandBus.dispatch(UpdateMunicipalityCommand(municipalityId, keyCode, name, federalEntityId!!))
                _formState.value = FormState.SuccessEdit
            } catch (e: Exception) {
                _formState.value = FormState.Error(e.cause!!)
            }
        }
    }

    fun deleteMunicipality(municipalityId: String) {
        _deleteState.value = DeleteState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            try {
                commandBus.dispatch(DeleteMunicipalityCommand(municipalityId))
                _deleteState.value = DeleteState.Success
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(e.cause!!)
            }
        }
    }
}
