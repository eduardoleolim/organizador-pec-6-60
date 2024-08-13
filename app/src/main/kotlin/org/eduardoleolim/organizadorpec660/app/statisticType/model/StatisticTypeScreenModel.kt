package org.eduardoleolim.organizadorpec660.app.statisticType.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.statisticType.data.EmptyStatisticTypeDataException
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.application.create.CreateStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.core.statisticType.application.delete.DeleteStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.core.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.eduardoleolim.organizadorpec660.core.statisticType.application.update.UpdateStatisticTypeCommand

sealed class StatisticTypeFormState {
    data object Idle : StatisticTypeFormState()
    data object InProgress : StatisticTypeFormState()
    data object SuccessCreate : StatisticTypeFormState()
    data object SuccessEdit : StatisticTypeFormState()
    data class Error(val error: Throwable) : StatisticTypeFormState()
}

sealed class StatisticTypeDeleteState {
    data object Idle : StatisticTypeDeleteState()
    data object InProgress : StatisticTypeDeleteState()
    data object Success : StatisticTypeDeleteState()
    data class Error(val error: Throwable) : StatisticTypeDeleteState()
}


class StatisticTypeScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var statisticTypes by mutableStateOf(StatisticTypesResponse(emptyList(), 0, null, null))
        private set

    var formState by mutableStateOf<StatisticTypeFormState>(StatisticTypeFormState.Idle)
        private set

    var deleteState by mutableStateOf<StatisticTypeDeleteState>(StatisticTypeDeleteState.Idle)
        private set

    fun resetForm() {
        formState = StatisticTypeFormState.Idle
    }

    fun resetDeleteModal() {
        deleteState = StatisticTypeDeleteState.Idle
    }

    fun searchStatisticTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
    ) {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchStatisticTypesByTermQuery(search, orders, limit, offset)
                statisticTypes = queryBus.ask(query)
            } catch (e: Exception) {
                statisticTypes = StatisticTypesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun createStatisticType(keyCode: String, name: String) {
        screenModelScope.launch(dispatcher) {
            formState = StatisticTypeFormState.InProgress
            delay(500)

            val isKeyCodeBlank = keyCode.isBlank()
            val isNameBlank = name.isBlank()

            if (isKeyCodeBlank || isNameBlank) {
                formState = StatisticTypeFormState.Error(EmptyStatisticTypeDataException(isKeyCodeBlank, isNameBlank))
                return@launch
            }

            try {
                commandBus.dispatch(CreateStatisticTypeCommand(keyCode, name)).fold(
                    ifRight = {
                        formState = StatisticTypeFormState.SuccessCreate
                    },
                    ifLeft = {
                        formState = StatisticTypeFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                formState = StatisticTypeFormState.Error(e.cause!!)
            }
        }
    }

    fun editStatisticType(statisticTypeId: String, keyCode: String, name: String) {
        screenModelScope.launch(dispatcher) {
            formState = StatisticTypeFormState.InProgress
            delay(500)

            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isKeyCodeEmpty || isNameEmpty) {
                formState = StatisticTypeFormState.Error(EmptyStatisticTypeDataException(isKeyCodeEmpty, isNameEmpty))
                return@launch
            }

            try {
                commandBus.dispatch(UpdateStatisticTypeCommand(statisticTypeId, keyCode, name)).fold(
                    ifRight = {
                        formState = StatisticTypeFormState.SuccessEdit
                    },
                    ifLeft = {
                        formState = StatisticTypeFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                formState = StatisticTypeFormState.Error(e.cause!!)
            }
        }
    }

    fun deleteStatisticType(statisticTypeId: String) {
        screenModelScope.launch(dispatcher) {
            deleteState = StatisticTypeDeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteStatisticTypeCommand(statisticTypeId)).fold(
                    ifRight = {
                        deleteState = StatisticTypeDeleteState.Success
                    },
                    ifLeft = {
                        deleteState = StatisticTypeDeleteState.Error(it)
                    }
                )
            } catch (e: Exception) {
                deleteState = StatisticTypeDeleteState.Error(e.cause!!)
            }
        }
    }
}
