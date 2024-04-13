package org.eduardoleolim.organizadorpec660.app.instrumentType.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.instrumentType.data.EmptyInstrumentTypeDataException
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.InstrumentTypesResponse
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.create.CreateInstrumentTypeCommand
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.delete.DeleteInstrumentTypeCommand
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQuery
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.update.UpdateInstrumentTypeCommand
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

class InstrumentTypeScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    var instrumentTypes by mutableStateOf(InstrumentTypesResponse(emptyList(), 0, null, null))
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

    fun searchInstrumentTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchInstrumentTypesByTermQuery(search, orders, limit, offset)
                instrumentTypes = queryBus.ask(query)
            } catch (e: Exception) {
                instrumentTypes = InstrumentTypesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun createInstrumentType(name: String) {
        screenModelScope.launch(Dispatchers.IO) {
            formState = FormState.InProgress
            delay(500)

            if (name.isEmpty()) {
                formState = FormState.Error(EmptyInstrumentTypeDataException())
                return@launch
            }

            try {
                commandBus.dispatch(CreateInstrumentTypeCommand(name))
                formState = FormState.SuccessCreate
            } catch (e: Exception) {
                formState = FormState.Error(e.cause!!)
            }
        }
    }

    fun editInstrumentType(instrumentTypeId: String, name: String) {
        screenModelScope.launch(Dispatchers.IO) {
            formState = FormState.InProgress
            delay(500)

            if (name.isEmpty()) {
                formState = FormState.Error(EmptyInstrumentTypeDataException())
                return@launch
            }

            try {
                commandBus.dispatch(UpdateInstrumentTypeCommand(instrumentTypeId, name))
                formState = FormState.SuccessCreate
            } catch (e: Exception) {
                formState = FormState.Error(e.cause!!)
            }
        }
    }

    fun deleteInstrumentType(instrumentTypeId: String) {
        screenModelScope.launch(Dispatchers.IO) {
            deleteState = DeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteInstrumentTypeCommand(instrumentTypeId))
                deleteState = DeleteState.Success
            } catch (e: Exception) {
                deleteState = DeleteState.Error(e.cause!!)
            }
        }
    }
}
