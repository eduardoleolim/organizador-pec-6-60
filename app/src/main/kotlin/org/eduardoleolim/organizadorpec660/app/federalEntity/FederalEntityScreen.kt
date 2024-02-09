package org.eduardoleolim.organizadorpec660.app.federalEntity

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.PaginatedDataTableState
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
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
        var showDeleteModel by remember { mutableStateOf(false) }
        var selectedFederalEntity by remember { mutableStateOf<FederalEntityResponse?>(null) }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var federalEntitiesResponse by remember { mutableStateOf(FederalEntitiesResponse(emptyList(), 0, null, null)) }
        var searchValue by remember { mutableStateOf("") }

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
                showDeleteModel = true
            }
        )

        selectedFederalEntity?.takeIf { showDeleteModel }?.let {
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
                                searchValue = ""
                                state.pageIndex = -1
                                state.pageSize = pageSizes.first()
                                showDeleteModel = false
                                selectedFederalEntity = null
                            },
                            onFailure = {
                                println(it.localizedMessage)
                                searchValue = ""
                                state.pageIndex = -1
                                state.pageSize = pageSizes.first()
                                showDeleteModel = false
                                selectedFederalEntity = null
                            }
                        )
                    }
                },
                onDismissRequest = {
                    searchValue = ""
                    state.pageIndex = -1
                    state.pageSize = pageSizes.first()
                    showDeleteModel = false
                    selectedFederalEntity = null
                }
            )
        }
    }

    @Composable
    private fun FederalEntitiesTable(
        value: String,
        onValueChange: (String) -> Unit,
        pageSizes: List<Int>,
        data: FederalEntitiesResponse,
        state: PaginatedDataTableState,
        onSearch: (search: String, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
        onDeleteRequest: (FederalEntityResponse) -> Unit
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
                            text = federalEntity.updatedAt?.toLocalDateTime()?.format(dateTimeFormatter) ?: "N/A"
                        )
                    }

                    cell {
                        IconButton(
                            onClick = {
                                println("Edit federal entity ${federalEntity.id}")
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
