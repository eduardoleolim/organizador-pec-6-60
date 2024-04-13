package org.eduardoleolim.organizadorpec660.app.statisticType.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.statisticType.data.EmptyStatisticTypeDataException
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.InstrumentTypeResponse
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.InstrumentTypesResponse
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.application.create.CreateStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.core.statisticType.application.delete.DeleteStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.core.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.eduardoleolim.organizadorpec660.core.statisticType.application.update.UpdateStatisticTypeCommand


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
    var statisticTypes by mutableStateOf(StatisticTypesResponse(emptyList(), 0, null, null))
        private set

    var instrumentTypes by mutableStateOf(emptyList<InstrumentTypeResponse>())
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

    fun searchStatisticTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchStatisticTypesByTermQuery(search, orders, limit, offset)
                statisticTypes = queryBus.ask(query)
            } catch (e: Exception) {
                statisticTypes = StatisticTypesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun searchAllInstrumentTypes() {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchInstrumentTypesByTermQuery()
                instrumentTypes = queryBus.ask<InstrumentTypesResponse>(query).instrumentTypes
            } catch (e: Exception) {
                instrumentTypes = emptyList()
            }
        }
    }

    fun createStatisticType(keyCode: String, name: String) {
        screenModelScope.launch(Dispatchers.IO) {
            formState = FormState.InProgress
            delay(500)

            val isKeyCodeBlank = keyCode.isBlank()
            val isNameBlank = name.isBlank()

            if (isKeyCodeBlank || isNameBlank) {
                formState = FormState.Error(EmptyStatisticTypeDataException(isKeyCodeBlank, isNameBlank))
                return@launch
            }

            try {
                commandBus.dispatch(CreateStatisticTypeCommand(keyCode, name))
                formState = FormState.SuccessCreate
            } catch (e: Exception) {
                formState = FormState.Error(e.cause!!)
            }
        }
    }

    fun editStatisticType(statisticTypeId: String, keyCode: String, name: String) {
        screenModelScope.launch(Dispatchers.IO) {
            formState = FormState.InProgress
            delay(500)

            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isKeyCodeEmpty || isNameEmpty) {
                formState = FormState.Error(EmptyStatisticTypeDataException(isKeyCodeEmpty, isNameEmpty))
                return@launch
            }

            try {
                commandBus.dispatch(UpdateStatisticTypeCommand(statisticTypeId, keyCode, name))
                formState = FormState.SuccessEdit
            } catch (e: Exception) {
                formState = FormState.Error(e.cause!!)
            }
        }
    }

    fun deleteStatisticType(statisticTypeId: String) {
        screenModelScope.launch(Dispatchers.IO) {
            deleteState = DeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteStatisticTypeCommand(statisticTypeId))
                deleteState = DeleteState.Success
            } catch (e: Exception) {
                deleteState = DeleteState.Error(e.cause!!)
            }
        }
    }
}
