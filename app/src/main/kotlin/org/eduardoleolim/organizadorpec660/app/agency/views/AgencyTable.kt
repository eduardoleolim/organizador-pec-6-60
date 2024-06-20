package org.eduardoleolim.organizadorpec660.app.agency.views

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
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.app.shared.composables.PlainTextTooltip
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortAscending
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortColumnIndex
import org.eduardoleolim.organizadorpec660.core.agency.application.AgenciesResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AgencyScreen.AgenciesTable(
    value: String,
    onValueChange: (String) -> Unit,
    pageSizes: List<Int>,
    data: AgenciesResponse,
    state: PaginatedDataTableState,
    onSearch: (search: String, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
    onDeleteRequest: (AgencyResponse) -> Unit,
    onEditRequest: (AgencyResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val orders = listOf("name", "consecutive", "municipality.keyCode", "createdAt", "updatedAt")
    val nameColumnName = stringResource(Res.string.ag_name)
    val consecutiveColumnName = stringResource(Res.string.ag_consecutive)
    val municipalityColumnName = stringResource(Res.string.ag_municipality)
    val createdAtColumnName = stringResource(Res.string.ag_created_at)
    val updatedAtColumnName = stringResource(Res.string.ag_updated_at)
    val actionsColumnName = stringResource(Res.string.table_col_actions)

    val columns = remember {
        fun onSort(index: Int, ascending: Boolean) {
            state.sortColumnIndex = index
            state.sortAscending = ascending
        }

        listOf(
            DataColumn(
                onSort = ::onSort,
                width = TableColumnWidth.Fraction(0.25f)
            ) {
                Text(nameColumnName)
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.MinIntrinsic
            ) {
                Text(consecutiveColumnName)
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.2f)
            ) {
                Text(municipalityColumnName)
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.18f)
            ) {
                Text(
                    text = createdAtColumnName,
                    textAlign = TextAlign.Center
                )
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.18f)
            ) {
                Text(
                    text = updatedAtColumnName,
                    textAlign = TextAlign.Center
                )
            },
            DataColumn(
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.18f)
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
            state = state,
            pageSizes = pageSizes,
            onSearch = { search, pageIndex, pageSize, sortBy, isAscending ->
                onSearch(search, pageIndex, pageSize, sortBy?.let { orders[it] }, isAscending)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            data.agencies.forEach { agency ->
                row {
                    cell {
                        Text(agency.name)
                    }
                    cell {
                        Text(agency.consecutive)
                    }
                    cell {
                        val municipality = agency.municipality

                        Text("${municipality.keyCode} - ${municipality.name}")
                    }

                    cell {
                        Text(agency.createdAt.toLocalDateTime().format(dateTimeFormatter))
                    }
                    cell {
                        Text(agency.updatedAt?.toLocalDateTime()?.format(dateTimeFormatter) ?: "N/A")
                    }

                    cell {
                        PlainTextTooltip(
                            tooltip = { Text(stringResource(Res.string.edit)) }
                        ) {
                            IconButton(
                                onClick = { onEditRequest(agency) }
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
                                onClick = { onDeleteRequest(agency) }
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
