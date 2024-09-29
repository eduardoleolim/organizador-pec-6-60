package org.eduardoleolim.organizadorpec660.statisticType.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.st_delete_error_default
import org.eduardoleolim.organizadorpec660.shared.resources.st_delete_error_not_found
import org.eduardoleolim.organizadorpec660.shared.resources.st_delete_error_used_in_agency
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.create.CreateStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.statisticType.application.delete.DeleteStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.statisticType.application.searchById.SearchStatisticTypeByIdQuery
import org.eduardoleolim.organizadorpec660.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.eduardoleolim.organizadorpec660.statisticType.application.update.UpdateStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.statisticType.data.EmptyStatisticTypeDataException
import org.eduardoleolim.organizadorpec660.statisticType.domain.CanNotDeleteStatisticTypeError
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeUsedInAgency
import org.jetbrains.compose.resources.getString

@OptIn(FlowPreview::class)
class StatisticTypeScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var screenState by mutableStateOf(StatisticTypeScreenState())
        private set

    private var _searchParameters = MutableStateFlow(StatisticTypeSearchParameters())

    val searchParameters: StateFlow<StatisticTypeSearchParameters> = _searchParameters

    var statisticTypes by mutableStateOf(StatisticTypesResponse(emptyList(), 0, null, null))
        private set

    var formState by mutableStateOf<StatisticTypeFormState>(StatisticTypeFormState.Idle)
        private set

    var deleteState by mutableStateOf<StatisticTypeDeleteState>(StatisticTypeDeleteState.Idle)
        private set

    var statisticType by mutableStateOf(StatisticTypeFormData())
        private set

    init {
        screenModelScope.launch(dispatcher) {
            _searchParameters
                .debounce(500)
                .collectLatest {
                    fetchStatisticTypes(it)
                }
        }
    }

    fun showFormModal(statisticType: StatisticTypeResponse?) {
        screenState = screenState.copy(
            selectedStatisticType = statisticType,
            showFormModal = true,
            showDeleteModal = false
        )
    }

    fun showDeleteModal(statisticType: StatisticTypeResponse) {
        screenState = screenState.copy(
            selectedStatisticType = statisticType,
            showFormModal = false,
            showDeleteModal = true
        )
    }

    fun searchStatisticType(statisticTypeId: String?) {
        screenModelScope.launch(dispatcher) {
            statisticType = if (statisticTypeId == null) {
                StatisticTypeFormData()
            } else {
                val statisticType = queryBus.ask<StatisticTypeResponse>(SearchStatisticTypeByIdQuery(statisticTypeId))
                StatisticTypeFormData(statisticType.id, statisticType.name, statisticType.keyCode)
            }
        }
    }

    fun updateStatisticTypeName(name: String) {
        statisticType = statisticType.copy(name = name)
    }

    fun updateStatisticTypeKeyCode(keyCode: String) {
        statisticType = statisticType.copy(keyCode = keyCode)
    }

    fun resetScreen() {
        screenModelScope.launch(dispatcher) {
            screenState = StatisticTypeScreenState()
            val limit = screenState.tableState.pageSize
            val offset = screenState.tableState.pageIndex * limit
            _searchParameters.value = StatisticTypeSearchParameters(limit = limit, offset = offset)
            fetchStatisticTypes(searchParameters.value)
        }
    }

    fun resetFormModal() {
        formState = StatisticTypeFormState.Idle
    }

    fun resetDeleteModal() {
        deleteState = StatisticTypeDeleteState.Idle
    }

    fun searchStatisticTypes(
        search: String = searchParameters.value.search,
        orders: List<HashMap<String, String>> = searchParameters.value.orders,
        limit: Int? = searchParameters.value.limit,
        offset: Int? = searchParameters.value.offset,
    ) {
        _searchParameters.value = StatisticTypeSearchParameters(search, orders, limit, offset)
    }

    private suspend fun fetchStatisticTypes(parameters: StatisticTypeSearchParameters) {
        withContext(dispatcher) {
            val (search, orders, limit, offset) = parameters
            try {
                val query = SearchStatisticTypesByTermQuery(search, orders.toTypedArray(), limit, offset)
                statisticTypes = queryBus.ask(query)
            } catch (e: Exception) {
                statisticTypes = StatisticTypesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun saveStatisticType() {
        screenModelScope.launch(dispatcher) {
            val (id, name, keyCode) = statisticType

            formState = StatisticTypeFormState.InProgress
            delay(500)

            val isKeyCodeBlank = keyCode.isBlank()
            val isNameBlank = name.isBlank()

            if (isKeyCodeBlank || isNameBlank) {
                formState = StatisticTypeFormState.Error(EmptyStatisticTypeDataException(isKeyCodeBlank, isNameBlank))
                return@launch
            }

            if (id == null) {
                createStatisticType(keyCode, name)
            } else {
                updateStatisticType(id, keyCode, name)
            }
        }
    }

    private suspend fun createStatisticType(keyCode: String, name: String) {
        withContext(dispatcher) {
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

    private suspend fun updateStatisticType(statisticTypeId: String, keyCode: String, name: String) {
        withContext(dispatcher) {
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

            deleteState = try {
                commandBus.dispatch(DeleteStatisticTypeCommand(statisticTypeId)).fold(
                    ifRight = {
                        StatisticTypeDeleteState.Success
                    },
                    ifLeft = { error ->
                        val message = when (error) {
                            is StatisticTypeNotFoundError -> getString(Res.string.st_delete_error_not_found)
                            is StatisticTypeUsedInAgency -> getString(Res.string.st_delete_error_used_in_agency)
                            is CanNotDeleteStatisticTypeError -> getString(Res.string.st_delete_error_default)
                            else -> getString(Res.string.st_delete_error_default)
                        }

                        StatisticTypeDeleteState.Error(message)
                    }
                )
            } catch (e: Exception) {
                StatisticTypeDeleteState.Error(getString(Res.string.st_delete_error_default))
            }
        }
    }
}
