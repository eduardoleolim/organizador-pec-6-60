package org.eduardoleolim.organizadorpec660.federalEntity.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.create.CreateFederalEntityCommand
import org.eduardoleolim.organizadorpec660.federalEntity.application.delete.DeleteFederalEntityCommand
import org.eduardoleolim.organizadorpec660.federalEntity.application.importer.CsvImportFederalEntitiesCommand
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchById.SearchFederalEntityByIdQuery
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.federalEntity.application.update.UpdateFederalEntityCommand
import org.eduardoleolim.organizadorpec660.federalEntity.data.EmptyFederalEntityDataException
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityHasMunicipalitiesError
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.services.KotlinCsvFederalEntityImportInput
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.getString
import java.io.File
import java.io.IOException

@OptIn(FlowPreview::class)
class FederalEntityScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    private val csvWriter = csvWriter()

    var screenState by mutableStateOf(FederalEntityScreenState())

    var searchParameters = MutableStateFlow(FederalEntitySearchParameters())
        private set

    var federalEntities by mutableStateOf(FederalEntitiesResponse(emptyList(), 0, null, null))
        private set

    var formState by mutableStateOf<FederalEntityFormState>(FederalEntityFormState.Idle)
        private set

    var deleteState by mutableStateOf<FederalEntityDeleteState>(FederalEntityDeleteState.Idle)
        private set

    var importState by mutableStateOf<FederalEntityImportState>(FederalEntityImportState.Idle)
        private set

    var federalEntity by mutableStateOf(FederalEntityFormData())
        private set

    init {
        screenModelScope.launch(dispatcher) {
            searchParameters
                .debounce(500)
                .collectLatest {
                    fetchFederalEntities(it)
                }
        }
    }

    fun showFormModal(federalEntity: FederalEntityResponse?) {
        screenState = screenState.copy(
            selectedFederalEntity = federalEntity,
            showFormModal = true,
            showDeleteModal = false,
            showImportExportModal = false,
            showImportModal = false,
            showExportModal = false
        )
    }

    fun showDeleteModal(federalEntity: FederalEntityResponse) {
        screenState = screenState.copy(
            selectedFederalEntity = federalEntity,
            showFormModal = false,
            showDeleteModal = true,
            showImportExportModal = false,
            showImportModal = false,
            showExportModal = false
        )
    }

    fun showImportExportModal() {
        screenState = screenState.copy(
            selectedFederalEntity = null,
            showFormModal = false,
            showDeleteModal = false,
            showImportExportModal = true,
            showImportModal = false,
            showExportModal = false
        )
    }

    fun showImportModal() {
        screenState = screenState.copy(
            selectedFederalEntity = null,
            showFormModal = false,
            showDeleteModal = false,
            showImportExportModal = false,
            showImportModal = true,
            showExportModal = false
        )
    }

    fun showExportModal() {
        screenState = screenState.copy(
            selectedFederalEntity = null,
            showFormModal = false,
            showDeleteModal = false,
            showImportExportModal = false,
            showImportModal = false,
            showExportModal = true
        )
    }

    fun searchFederalEntity(federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            federalEntity = if (federalEntityId == null) {
                FederalEntityFormData()
            } else {
                val federalEntity = queryBus.ask<FederalEntityResponse>(SearchFederalEntityByIdQuery(federalEntityId))
                FederalEntityFormData(federalEntity.id, federalEntity.name, federalEntity.keyCode)
            }
        }
    }

    fun updateFederalEntityName(name: String) {
        federalEntity = federalEntity.copy(name = name)
    }

    fun updateFederalEntityKeyCode(keyCode: String) {
        federalEntity = federalEntity.copy(keyCode = keyCode)
    }

    fun resetScreen() {
        screenModelScope.launch(dispatcher) {
            screenState = FederalEntityScreenState()
            val limit = screenState.tableState.pageSize
            val offset = screenState.tableState.pageIndex * limit
            searchParameters.value = FederalEntitySearchParameters(limit = limit, offset = offset)
            fetchFederalEntities(searchParameters.value)
        }
    }

    fun resetFormModal() {
        formState = FederalEntityFormState.Idle
        federalEntity = FederalEntityFormData()
    }

    fun resetDeleteModal() {
        deleteState = FederalEntityDeleteState.Idle
    }

    fun resetImportModal() {
        importState = FederalEntityImportState.Idle
    }

    fun searchFederalEntities(
        search: String = searchParameters.value.search,
        orders: List<HashMap<String, String>> = searchParameters.value.orders,
        limit: Int? = searchParameters.value.limit,
        offset: Int? = searchParameters.value.offset,
    ) {
        searchParameters.value = FederalEntitySearchParameters(search, orders, limit, offset)
    }

    private suspend fun fetchFederalEntities(parameters: FederalEntitySearchParameters) {
        withContext(dispatcher) {
            val (search, orders, limit, offset) = parameters
            try {
                val query = SearchFederalEntitiesByTermQuery(search, orders.toTypedArray(), limit, offset)
                federalEntities = queryBus.ask(query)
            } catch (e: Exception) {
                federalEntities = FederalEntitiesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun saveFederalEntity() {
        screenModelScope.launch(dispatcher) {
            val (id, name, keyCode) = federalEntity
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            formState = FederalEntityFormState.InProgress
            delay(500)

            if (isKeyCodeEmpty || isNameEmpty) {
                formState = FederalEntityFormState.Error(EmptyFederalEntityDataException(isKeyCodeEmpty, isNameEmpty))
                return@launch
            }

            if (id == null) {
                createFederalEntity(keyCode, name)
            } else {
                updateFederalEntity(id, keyCode, name)
            }
        }
    }

    private suspend fun createFederalEntity(keyCode: String, name: String) {
        withContext(dispatcher) {
            formState = try {
                val command = CreateFederalEntityCommand(keyCode, name)
                commandBus.dispatch(command).fold(
                    ifRight = {
                        FederalEntityFormState.SuccessCreate
                    },
                    ifLeft = {
                        FederalEntityFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                FederalEntityFormState.Error(e.cause!!)
            }
        }
    }

    private suspend fun updateFederalEntity(id: String, keyCode: String, name: String) {
        withContext(dispatcher) {
            formState = try {
                val command = UpdateFederalEntityCommand(id, keyCode, name)
                commandBus.dispatch(command).fold(
                    ifRight = {
                        FederalEntityFormState.SuccessEdit
                    },
                    ifLeft = {
                        FederalEntityFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                FederalEntityFormState.Error(e.cause!!)
            }
        }
    }

    fun deleteFederalEntity(federalEntityId: String) {
        screenModelScope.launch(dispatcher) {
            deleteState = FederalEntityDeleteState.InProgress
            delay(500)

            deleteState = try {
                val command = DeleteFederalEntityCommand(federalEntityId)
                commandBus.dispatch(command)
                    .foldAsync(
                        ifRight = {
                            FederalEntityDeleteState.Success
                        },
                        ifLeft = { error ->
                            val message = when (error) {
                                is FederalEntityHasMunicipalitiesError -> getString(Res.string.fe_delete_error_has_municipalities)
                                is FederalEntityNotFoundError -> getString(Res.string.fe_delete_error_not_found)
                                else -> getString(Res.string.fe_delete_error_default)
                            }

                            FederalEntityDeleteState.Error(message)
                        }
                    )
            } catch (e: Exception) {
                FederalEntityDeleteState.Error(getString(Res.string.fe_delete_error_default))
            }
        }
    }

    fun saveTemplate(file: File) {
        screenModelScope.launch(dispatcher) {
            val keyCode = getString(Res.string.fe_keycode)
            val name = getString(Res.string.fe_name)

            try {
                csvWriter.open(file) {
                    writeRow(listOf(keyCode, name))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun importFederalEntities(file: File) {
        screenModelScope.launch(dispatcher) {
            val keyCodeHeader = getString(Res.string.fe_keycode)
            val nameHeader = getString(Res.string.fe_name)
            importState = FederalEntityImportState.InProgress
            delay(500)

            val input = KotlinCsvFederalEntityImportInput(file, keyCodeHeader, nameHeader)
            val command = CsvImportFederalEntitiesCommand(input, true)
            importState = commandBus.dispatch(command)
                .fold(
                    ifRight = { warnings ->
                        FederalEntityImportState.Success(warnings.map { it.error })
                    },
                    ifLeft = {
                        FederalEntityImportState.Error(it)
                    }
                )
        }
    }
}
