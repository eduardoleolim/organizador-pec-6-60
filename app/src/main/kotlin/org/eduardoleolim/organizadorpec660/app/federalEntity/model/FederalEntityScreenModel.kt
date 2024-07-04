package org.eduardoleolim.organizadorpec660.app.federalEntity.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.federalEntity.data.EmptyFederalEntityDataException
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.create.CreateFederalEntityCommand
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.delete.DeleteFederalEntityCommand
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.update.UpdateFederalEntityCommand
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

sealed class FederalEntityFormState {
    data object Idle : FederalEntityFormState()
    data object InProgress : FederalEntityFormState()
    data object SuccessCreate : FederalEntityFormState()
    data object SuccessEdit : FederalEntityFormState()
    data class Error(val error: Throwable) : FederalEntityFormState()
}

sealed class FederalEntityDeleteState {
    data object Idle : FederalEntityDeleteState()
    data object InProgress : FederalEntityDeleteState()
    data object Success : FederalEntityDeleteState()
    data class Error(val error: Throwable) : FederalEntityDeleteState()
}

class FederalEntityScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    var federalEntities by mutableStateOf(FederalEntitiesResponse(emptyList(), 0, null, null))
        private set

    var formState by mutableStateOf<FederalEntityFormState>(FederalEntityFormState.Idle)
        private set

    var deleteState by mutableStateOf<FederalEntityDeleteState>(FederalEntityDeleteState.Idle)
        private set

    fun resetForm() {
        formState = FederalEntityFormState.Idle
    }

    fun resetDeleteModal() {
        deleteState = FederalEntityDeleteState.Idle
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
                federalEntities = queryBus.ask(query)
            } catch (e: Exception) {
                federalEntities = FederalEntitiesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun createFederalEntity(keyCode: String, name: String) {
        screenModelScope.launch(Dispatchers.IO) {
            formState = FederalEntityFormState.InProgress
            delay(500)

            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isKeyCodeEmpty || isNameEmpty) {
                formState = FederalEntityFormState.Error(EmptyFederalEntityDataException(isKeyCodeEmpty, isNameEmpty))
                return@launch
            }

            try {
                commandBus.dispatch(CreateFederalEntityCommand(keyCode, name))
                formState = FederalEntityFormState.SuccessCreate
            } catch (e: Exception) {
                formState = FederalEntityFormState.Error(e.cause!!)
            }
        }
    }

    fun editFederalEntity(federalEntityId: String, keyCode: String, name: String) {
        screenModelScope.launch(Dispatchers.IO) {
            formState = FederalEntityFormState.InProgress
            delay(500)

            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isKeyCodeEmpty || isNameEmpty) {
                formState = FederalEntityFormState.Error(EmptyFederalEntityDataException(isKeyCodeEmpty, isNameEmpty))
                return@launch
            }

            try {
                commandBus.dispatch(UpdateFederalEntityCommand(federalEntityId, keyCode, name))
                formState = FederalEntityFormState.SuccessEdit
            } catch (e: Exception) {
                formState = FederalEntityFormState.Error(e.cause!!)
            }
        }
    }

    fun deleteFederalEntity(federalEntityId: String) {
        screenModelScope.launch(Dispatchers.IO) {
            deleteState = FederalEntityDeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteFederalEntityCommand(federalEntityId))
                deleteState = FederalEntityDeleteState.Success
            } catch (e: Exception) {
                deleteState = FederalEntityDeleteState.Error(e.cause!!)
            }
        }
    }
}
