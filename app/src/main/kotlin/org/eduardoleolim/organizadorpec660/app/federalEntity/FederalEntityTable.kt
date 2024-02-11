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
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorpec660.app.shared.components.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun FederalEntityScreen.FederalEntitiesTable(
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
                onSort = ::onSort,
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
        modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()
    ) {
        val offset = data.offset ?: 0
        val filteredRecords = data.filtered
        val totalRecords = data.total
        val remainingRows = (totalRecords - offset) - filteredRecords

        // Add necessary rows for pagination
        repeat(offset) {
            row {
                // Necessary to avoid an index out of bounds exception
                // It is an issue with the library used to create the table
                cell { }
            }
        }

        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        data.federalEntities.forEach { federalEntity ->
            row {
                cell {
                    Text(federalEntity.keyCode)
                }
                cell {
                    Text(federalEntity.name)
                }

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
