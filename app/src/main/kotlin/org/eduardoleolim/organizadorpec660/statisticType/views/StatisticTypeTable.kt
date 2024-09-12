package org.eduardoleolim.organizadorpec660.statisticType.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorpec660.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.shared.composables.PlainTextTooltip
import org.eduardoleolim.organizadorpec660.shared.composables.sortAscending
import org.eduardoleolim.organizadorpec660.shared.composables.sortColumnIndex
import org.eduardoleolim.organizadorpec660.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypesResponse
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
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
    val orders = remember { listOf("keyCode", "name", "createdAt", "updatedAt") }
    val keyCodeColumnName = stringResource(Res.string.st_keycode)
    val nameColumnName = stringResource(Res.string.st_name)
    val createdAtColumnName = stringResource(Res.string.st_created_at)
    val updatedAtColumnName = stringResource(Res.string.st_updated_at)
    val actionsColumnName = stringResource(Res.string.table_col_actions)

    val columns = remember {
        fun onSort(index: Int, ascending: Boolean) {
            state.sortColumnIndex = index
            state.sortAscending = ascending
        }

        listOf(
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.MinIntrinsic,
                header = { Text(keyCodeColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                header = { Text(nameColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.2f),
                header = {
                    Text(
                        text = createdAtColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.2f),
                header = {
                    Text(
                        text = updatedAtColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.2f),
                header = {
                    Text(actionsColumnName)
                }
            )
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
                        Text(statisticType.createdAt.toLocalDateTime().format(dateTimeFormatter))
                    }
                    cell {
                        Text(statisticType.updatedAt?.toLocalDateTime()?.format(dateTimeFormatter) ?: "N/A")
                    }

                    cell {
                        PlainTextTooltip(
                            tooltip = {
                                Text(stringResource(Res.string.edit))
                            }
                        ) {
                            IconButton(
                                onClick = { onEditRequest(statisticType) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                        }

                        PlainTextTooltip(
                            tooltip = {
                                Text(stringResource(Res.string.delete))
                            }
                        ) {
                            IconButton(
                                onClick = { onDeleteRequest(statisticType) }
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
}
