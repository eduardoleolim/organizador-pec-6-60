package org.eduardoleolim.organizadorpec660.app.instrumentType.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.app.shared.composables.PlainTextTooltip
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortAscending
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortColumnIndex
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.InstrumentTypeResponse
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.InstrumentTypesResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
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
    val orders = listOf("name", "createdAt", "updatedAt")
    val nameColumnName = stringResource(Res.string.it_name)
    val createdAtColumnName = stringResource(Res.string.it_created_at)
    val updatedAtColumnName = stringResource(Res.string.it_updated_at)
    val actionsColumnName = stringResource(Res.string.table_col_actions)

    val columns = remember {
        fun onSort(index: Int, ascending: Boolean) {
            state.sortColumnIndex = index
            state.sortAscending = ascending
        }

        listOf(
            DataColumn(
                onSort = ::onSort,
            ) {
                Text(nameColumnName)
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.25f)
            ) {
                Text(
                    text = createdAtColumnName,
                    textAlign = TextAlign.Center
                )
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.25f)
            ) {
                Text(
                    text = updatedAtColumnName,
                    textAlign = TextAlign.Center
                )
            },
            DataColumn(
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.2f)
            ) {
                Text(actionsColumnName)
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
            sortColumnIndex = state.sortColumnIndex,
            sortAscending = state.sortAscending,
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
                        PlainTextTooltip(
                            tooltip = { Text(stringResource(Res.string.edit)) }
                        ) {
                            IconButton(
                                onClick = { onEditRequest(instrumentType) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                        }

                        PlainTextTooltip(
                            tooltip = { Text(stringResource(Res.string.delete)) }
                        ) {
                            IconButton(
                                onClick = { onDeleteRequest(instrumentType) }
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
