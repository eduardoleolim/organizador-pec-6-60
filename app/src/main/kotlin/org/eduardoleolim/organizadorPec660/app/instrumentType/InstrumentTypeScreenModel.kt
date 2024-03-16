package org.eduardoleolim.organizadorPec660.app.instrumentType

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypesResponse
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.create.CreateInstrumentTypeCommand
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.delete.DeleteInstrumentTypeCommand
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQuery
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.update.UpdateInstrumentTypeCommand
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

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
    private var _instrumentTypes = mutableStateOf(InstrumentTypesResponse(emptyList(), 0, null, null))
    val instrumentTypes: State<InstrumentTypesResponse> get() = _instrumentTypes

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

    fun searchInstrumentTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchInstrumentTypesByTermQuery(search, orders, limit, offset)
                _instrumentTypes.value = queryBus.ask(query)
            } catch (e: Exception) {
                _instrumentTypes.value = InstrumentTypesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun createInstrumentType(name: String) {
        _formState.value = FormState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            if (name.isBlank()) {
                _formState.value = FormState.Error(EmptyInstrumentTypeDataException())
                return@launch
            }

            try {
                commandBus.dispatch(CreateInstrumentTypeCommand(name))
                _formState.value = FormState.SuccessCreate
            } catch (e: Exception) {
                _formState.value = FormState.Error(e.cause!!)
            }
        }
    }

    fun editInstrumentType(instrumentTypeId: String, name: String) {
        _formState.value = FormState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            if (name.isBlank()) {
                _formState.value = FormState.Error(EmptyInstrumentTypeDataException())
                return@launch
            }

            try {
                commandBus.dispatch(UpdateInstrumentTypeCommand(instrumentTypeId, name))
                _formState.value = FormState.SuccessCreate
            } catch (e: Exception) {
                _formState.value = FormState.Error(e.cause!!)
            }
        }
    }

    fun deleteInstrumentType(instrumentTypeId: String) {
        _deleteState.value = DeleteState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            try {
                commandBus.dispatch(DeleteInstrumentTypeCommand(instrumentTypeId))
                _deleteState.value = DeleteState.Success
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(e.cause!!)
            }
        }
    }
}
