package org.eduardoleolim.organizadorpec660.app.federalEntity.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.federalEntity.data.EmptyFederalEntityDataException
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.create.CreateFederalEntityCommand
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.delete.DeleteFederalEntityCommand
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.update.UpdateFederalEntityCommand
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

class FederalEntityScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    private var _federalEntities = mutableStateOf(FederalEntitiesResponse(emptyList(), 0, null, null))
    val federalEntities: State<FederalEntitiesResponse> get() = _federalEntities

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

    fun searchFederalEntities(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchFederalEntitiesByTermQuery(search, orders, limit, offset)
                _federalEntities.value = queryBus.ask(query)
            } catch (e: Exception) {
                _federalEntities.value = FederalEntitiesResponse(emptyList(), 0, null, null)
                println(e.message)
            }
        }
    }

    fun createFederalEntity(keyCode: String, name: String) {
        _formState.value = FormState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            val isKeyCodeEmpty = keyCode.isBlank()
            val isNameBlank = name.isBlank()

            if (isKeyCodeEmpty || isNameBlank) {
                _formState.value = FormState.Error(EmptyFederalEntityDataException(isKeyCodeEmpty, isNameBlank))
                return@launch
            }

            try {
                commandBus.dispatch(CreateFederalEntityCommand(keyCode, name))
                _formState.value = FormState.SuccessCreate
            } catch (e: Exception) {
                _formState.value = FormState.Error(e.cause!!)
            }
        }
    }

    fun editFederalEntity(federalEntityId: String, keyCode: String, name: String) {
        _formState.value = FormState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            val isKeyCodeEmpty = keyCode.isBlank()
            val isNameBlank = name.isBlank()

            if (isKeyCodeEmpty || isNameBlank) {
                _formState.value = FormState.Error(EmptyFederalEntityDataException(isKeyCodeEmpty, isNameBlank))
                return@launch
            }

            try {
                commandBus.dispatch(UpdateFederalEntityCommand(federalEntityId, keyCode, name))
                _formState.value = FormState.SuccessEdit
            } catch (e: Exception) {
                _formState.value = FormState.Error(e.cause!!)
            }
        }
    }

    fun deleteFederalEntity(federalEntityId: String) {
        _deleteState.value = DeleteState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            try {
                commandBus.dispatch(DeleteFederalEntityCommand(federalEntityId))
                _deleteState.value = DeleteState.Success
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(e.cause!!)
            }
        }
    }
}
