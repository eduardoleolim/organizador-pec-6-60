package org.eduardoleolim.organizadorPec660.app.instrumentType

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorPec660.app.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypeResponse
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypesResponse
import org.eduardoleolim.organizadorPec660.core.shared.domain.toLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun InstrumentTypeScreen.InstrumentTypeTable(
    value: String,
    onValueChange: (String) -> Unit,
    pageSizes: List<Int>,
    data: InstrumentTypesResponse,
    state: PaginatedDataTableState,
    onSearch: (search: String, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
    onDeleteRequest: (InstrumentTypeResponse) -> Unit,
    onEditRequest: (InstrumentTypeResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var sortColumnIndex by remember { mutableStateOf<Int?>(null) }
    var sortAscending by remember { mutableStateOf(true) }
    val orders = listOf("name", "createdAt", "updatedAt")

    val columns = remember(sortColumnIndex, sortAscending) {
        fun onSort(index: Int, ascending: Boolean) {
            sortColumnIndex = index
            sortAscending = ascending
        }

        listOf(
            DataColumn(
                onSort = ::onSort,
            ) {
                Text("Nombre")
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.25f)
            ) {
                Text(
                    text = "Fecha de registro",
                    textAlign = TextAlign.Center
                )
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.25f)
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

    Surface(
        modifier = Modifier.then(modifier),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        PaginatedDataTable(
            total = data.total,
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
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {


            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            data.instrumentTypes.forEach { instrumentType ->
                row {
                    cell {
                        Text(instrumentType.name)
                    }
                    cell {
                        Text(
                            text = instrumentType.createdAt.toLocalDateTime().format(dateTimeFormatter)
                        )
                    }
                    cell {
                        Text(
                            text = instrumentType.updatedAt?.toLocalDateTime()?.format(dateTimeFormatter)
                                ?: "N/A"
                        )
                    }

                    cell {

                        IconButton(
                            onClick = {
                                onEditRequest(instrumentType)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }

                        IconButton(
                            onClick = {
                                onDeleteRequest(instrumentType)
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
        }
    }
}
