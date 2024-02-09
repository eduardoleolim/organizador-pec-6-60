package org.eduardoleolim.organizadorpec660.app.federalEntity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.PaginatedDataTableState
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import org.eduardoleolim.organizadorpec660.app.home.HomeActions
import org.eduardoleolim.organizadorpec660.app.home.HomeTitle
import org.eduardoleolim.organizadorpec660.app.shared.components.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.app.shared.components.dialogs.QuestionDialog
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import java.time.format.DateTimeFormatter

class FederalEntityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { FederalEntityScreenModel(queryBus, commandBus) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormDialog by remember { mutableStateOf(false) }
        var selectedFederalEntity by remember { mutableStateOf<FederalEntityResponse?>(null) }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var federalEntitiesResponse by remember { mutableStateOf(FederalEntitiesResponse(emptyList(), 0, null, null)) }
        var searchValue by remember { mutableStateOf("") }

        HomeTitle("Entidades federativas")
        HomeActions {
            SmallFloatingActionButton(
                onClick = { showFormDialog = true },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Filled.Add, "Agregar entidad federativa")
            }
        }

        fun resetTable() {
            searchValue = ""
            state.pageIndex = -1
            state.pageSize = pageSizes.first()
            showDeleteModal = false
            selectedFederalEntity = null
        }

        FederalEntitiesTable(
            value = searchValue,
            onValueChange = { searchValue = it },
            pageSizes = pageSizes,
            state = state,
            data = federalEntitiesResponse,
            onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                val order = orderBy?.let {
                    val orderType = if (isAscending) "ASC" else "DESC"
                    arrayOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                }

                screenModel.searchFederalEntities(
                    search = search,
                    limit = pageSize,
                    offset = pageIndex * pageSize,
                    orders = order
                ) { result ->
                    result.fold(
                        onSuccess = {
                            federalEntitiesResponse = it
                        },
                        onFailure = {
                            println(it.localizedMessage)
                            federalEntitiesResponse = FederalEntitiesResponse(emptyList(), 0, null, null)
                        }
                    )
                }
            },
            onDeleteRequest = { federalEntity ->
                selectedFederalEntity = federalEntity
                showDeleteModal = true
            },
            onEditRequest = { federalEntity ->
                selectedFederalEntity = federalEntity
                showFormDialog = true
            }
        )

        selectedFederalEntity?.takeIf { showDeleteModal }?.let {
            QuestionDialog(
                title = {
                    Text("Eliminar entidad federativa")
                },
                text = {
                    Text("¿Estás seguro de que deseas eliminar la entidad federativa ${selectedFederalEntity!!.name}?")
                },
                onConfirmRequest = {
                    screenModel.deleteFederalEntity(selectedFederalEntity!!.id) { result ->
                        result.fold(
                            onSuccess = {
                                resetTable()
                            },
                            onFailure = {
                                println(it.localizedMessage)
                                resetTable()
                            }
                        )
                    }
                },
                onDismissRequest = {
                    resetTable()
                }
            )
        }

        if (showFormDialog) {
            FederalEntityForm(
                screenModel = screenModel,
                selectedFederalEntity = selectedFederalEntity,
                onDismissRequest = {
                    showFormDialog = false
                    selectedFederalEntity = null
                    resetTable()
                },
                onSuccess = {
                    showFormDialog = false
                    selectedFederalEntity = null
                    resetTable()
                }
            )
        }
    }

    @Composable
    private fun FederalEntityForm(
        screenModel: FederalEntityScreenModel,
        selectedFederalEntity: FederalEntityResponse?,
        onDismissRequest: () -> Unit,
        onSuccess: () -> Unit
    ) {
        var federalEntityId by remember { mutableStateOf(selectedFederalEntity?.id) }
        var keyCode by remember { mutableStateOf(selectedFederalEntity?.keyCode ?: "") }
        var name by remember { mutableStateOf(selectedFederalEntity?.name ?: "") }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(selectedFederalEntity?.let { "Editar entidad federativa" } ?: "Agregar entidad federativa")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = keyCode,
                        onValueChange = {
                            if (Regex("[0-9]{0,2}").matches(it)) {
                                keyCode = it
                            }
                        },
                        label = { Text("Clave") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it.uppercase() },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (selectedFederalEntity != null) {
                            screenModel.editFederalEntity(federalEntityId!!, keyCode, name) { result ->
                                result.fold(
                                    onSuccess = {
                                        onSuccess()
                                    },
                                    onFailure = {
                                        println(it.localizedMessage)
                                    }
                                )
                            }
                        } else {
                            screenModel.createFederalEntity(keyCode, name) { result ->
                                result.fold(
                                    onSuccess = {
                                        onSuccess()
                                    },
                                    onFailure = {
                                        println(it.localizedMessage)
                                    }
                                )
                            }
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest,
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    @Composable
    private fun FederalEntitiesTable(
        value: String,
        onValueChange: (String) -> Unit,
        pageSizes: List<Int>,
        data: FederalEntitiesResponse,
        state: PaginatedDataTableState,
        onSearch: (search: String, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
        onDeleteRequest: (FederalEntityResponse) -> Unit,
        onEditRequest: (FederalEntityResponse) -> Unit
    ) {
        var sortColumnIndex by remember { mutableStateOf<Int?>(null) }
        var sortAscending by remember { mutableStateOf(true) }
        val orders = listOf("keyCode", "name", "createdAt", "updatedAt")

        val columns = remember(sortColumnIndex, sortAscending) {
            fun onSort(index: Int, ascending: Boolean) {
                sortColumnIndex = index
                sortAscending = ascending
            }

            listOf(
                DataColumn(
                    onSort = ::onSort,
                    alignment = Alignment.CenterHorizontally,
                    width = TableColumnWidth.MinIntrinsic
                ) {
                    Text("Clave")
                },
                DataColumn(
                    onSort = ::onSort,
                ) {
                    Text("Nombre")
                },
                DataColumn(
                    onSort = ::onSort,
                    alignment = Alignment.CenterHorizontally,
                    width = TableColumnWidth.Fraction(0.2f)
                ) {
                    Text(
                        text = "Fecha de registro",
                        textAlign = TextAlign.Center
                    )
                },
                DataColumn(
                    onSort = { index, ascending ->
                        sortColumnIndex = index
                        sortAscending = ascending
                    },
                    alignment = Alignment.CenterHorizontally,
                    width = TableColumnWidth.Fraction(0.2f)
                ) {
                    Text(
                        text = "Última actualización",
                        textAlign = TextAlign.Center
                    )
                },
                DataColumn(
                    alignment = Alignment.CenterHorizontally,
                    width = TableColumnWidth.Fraction(0.2f)
                ) {
                    Text("Acciones")
                }
            )
        }

        PaginatedDataTable(
            value = value,
            onValueChange = onValueChange,
            columns = columns,
            sortColumnIndex = sortColumnIndex,
            sortAscending = sortAscending,
            state = state,
            pageSizes = pageSizes,
            onSearch = { search, pageIndex, pageSize, sortBy, isAscending ->
                onSearch(search, pageIndex, pageSize, sortBy?.let { orders[it] }, isAscending)
            },
            modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth(),
        ) {
            val offset = data.offset ?: 0
            val filteredRecords = data.filteredRecords
            val totalRecords = data.totalRecords
            val remainingRows = (totalRecords - offset) - filteredRecords

            // Add necessary rows for pagination
            repeat(offset) {
                row {
                    // Necessary to avoid an index out of bounds exception
                    // It is an issue with the library used to create the table
                    cell { }
                }
            }

            data.federalEntities.forEach { federalEntity ->
                row {
                    cell {
                        Text(federalEntity.keyCode)
                    }
                    cell {
                        Text(federalEntity.name)
                    }

                    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    cell {
                        Text(
                            text = federalEntity.createdAt.toLocalDateTime().format(dateTimeFormatter)
                        )
                    }
                    cell {
                        Text(
                            text = federalEntity.updatedAt?.toLocalDateTime()?.format(dateTimeFormatter)
                                ?: "N/A"
                        )
                    }

                    cell {
                        IconButton(
                            onClick = {
                                onEditRequest(federalEntity)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }

                        IconButton(
                            onClick = {
                                onDeleteRequest(federalEntity)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }

            repeat(remainingRows) {
                row {
                    // Necessary to avoid an index out of bounds exception
                    // It is an issue with the library used to create the table
                    cell { }
                }
            }
        }
    }
}
