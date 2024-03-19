package org.eduardoleolim.organizadorPec660.app.statisticType

import androidx.compose.foundation.layout.fillMaxWidth
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
import org.eduardoleolim.organizadorPec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorPec660.core.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorPec660.core.statisticType.application.StatisticTypesResponse
import java.time.format.DateTimeFormatter

@Composable
fun StatisticTypeScreen.StatisticTypeTable(
    value: String,
    onValueChange: (String) -> Unit,
    pageSizes: List<Int>,
    data: StatisticTypesResponse,
    state: PaginatedDataTableState,
    onSearch: (search: String, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
    onDeleteRequest: (StatisticTypeResponse) -> Unit,
    onEditRequest: (StatisticTypeResponse) -> Unit,
    modifier: Modifier = Modifier
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
            modifier = Modifier.fillMaxWidth()
        ) {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            data.statisticTypes.forEach { statisticType ->
                row {
                    cell {
                        Text(statisticType.keyCode)
                    }
                    cell {
                        Text(statisticType.name)
                    }

                    cell {
                        Text(
                            text = statisticType.createdAt.toLocalDateTime().format(dateTimeFormatter)
                        )
                    }
                    cell {
                        Text(
                            text = statisticType.updatedAt?.toLocalDateTime()?.format(dateTimeFormatter)
                                ?: "N/A"
                        )
                    }

                    cell {
                        IconButton(
                            onClick = {
                                onEditRequest(statisticType)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }

                        IconButton(
                            onClick = {
                                onDeleteRequest(statisticType)
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
