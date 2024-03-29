package org.eduardoleolim.organizadorPec660.app.statisticType.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorPec660.app.statisticType.data.EmptyStatisticTypeDataException
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypeResponse
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypesResponse
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQuery
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorPec660.core.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorPec660.core.statisticType.application.create.CreateStatisticTypeCommand
import org.eduardoleolim.organizadorPec660.core.statisticType.application.delete.DeleteStatisticTypeCommand
import org.eduardoleolim.organizadorPec660.core.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.eduardoleolim.organizadorPec660.core.statisticType.application.update.UpdateStatisticTypeCommand


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


class StatisticTypeScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    private var _statisticTypes = mutableStateOf(StatisticTypesResponse(emptyList(), 0, null, null))
    val statisticTypes: State<StatisticTypesResponse> get() = _statisticTypes

    private var _instrumentTypes = mutableStateOf(emptyList<InstrumentTypeResponse>())
    val instrumentTypes: State<List<InstrumentTypeResponse>> get() = _instrumentTypes

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

    fun searchStatisticTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchStatisticTypesByTermQuery(search, orders, limit, offset)
                _statisticTypes.value = queryBus.ask(query)
            } catch (e: Exception) {
                _statisticTypes.value = StatisticTypesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun searchAllInstrumentTypes() {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchInstrumentTypesByTermQuery()
                _instrumentTypes.value = queryBus.ask<InstrumentTypesResponse>(query).instrumentTypes
            } catch (e: Exception) {
                _instrumentTypes.value = emptyList()
            }
        }
    }

    fun createStatisticType(keyCode: String, name: String, instrumentTypeIds: List<String>) {
        _formState.value = FormState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            val isKeyCodeBlank = keyCode.isBlank()
            val isNameBlank = name.isBlank()
            val isInstrumentTypesBlank = instrumentTypeIds.isEmpty()

            if (isKeyCodeBlank || isNameBlank) {
                _formState.value = FormState.Error(
                    EmptyStatisticTypeDataException(isKeyCodeBlank, isNameBlank, isInstrumentTypesBlank)
                )
                return@launch
            }

            try {
                commandBus.dispatch(CreateStatisticTypeCommand(keyCode, name, instrumentTypeIds))
                _formState.value = FormState.SuccessCreate
            } catch (e: Exception) {
                _formState.value = FormState.Error(e.cause!!)
            }
        }
    }

    fun editStatisticType(statisticTypeId: String, keyCode: String, name: String, instrumentTypeIds: List<String>) {
        _formState.value = FormState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            val isKeyCodeBlank = keyCode.isBlank()
            val isNameBlank = name.isBlank()
            val isInstrumentTypesBlank = instrumentTypeIds.isEmpty()

            if (isKeyCodeBlank || isNameBlank) {
                _formState.value = FormState.Error(
                    EmptyStatisticTypeDataException(isKeyCodeBlank, isNameBlank, isInstrumentTypesBlank)
                )
                return@launch
            }

            try {
                commandBus.dispatch(UpdateStatisticTypeCommand(statisticTypeId, keyCode, name, instrumentTypeIds))
                _formState.value = FormState.SuccessEdit
            } catch (e: Exception) {
                _formState.value = FormState.Error(e.cause!!)
            }
        }
    }

    fun deleteStatisticType(statisticTypeId: String) {
        _deleteState.value = DeleteState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            try {
                commandBus.dispatch(DeleteStatisticTypeCommand(statisticTypeId))
                _deleteState.value = DeleteState.Success
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(e.cause!!)
            }
        }
    }
}
