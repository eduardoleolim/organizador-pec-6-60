package org.eduardoleolim.organizadorpec660.federalEntity.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
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
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityFields
import org.eduardoleolim.organizadorpec660.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.shared.composables.PlainTextTooltip
import org.eduardoleolim.organizadorpec660.shared.composables.sortAscending
import org.eduardoleolim.organizadorpec660.shared.composables.sortColumnIndex
import org.eduardoleolim.organizadorpec660.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FederalEntityScreen.FederalEntitiesTable(
    value: String,
    pageSizes: List<Int>,
    data: FederalEntitiesResponse,
    state: PaginatedDataTableState,
    onSearch: (search: String, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
    onDeleteRequest: (FederalEntityResponse) -> Unit,
    onEditRequest: (FederalEntityResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyCodeColumnName = stringResource(Res.string.fe_keycode)
    val nameColumnName = stringResource(Res.string.fe_name)
    val createdAtColumnName = stringResource(Res.string.fe_created_at)
    val updatedAtColumnName = stringResource(Res.string.fe_updated_at)
    val actionsColumnName = stringResource(Res.string.table_col_actions)

    fun getOrderBy(columnIndex: Int?) = when (columnIndex) {
        0 -> FederalEntityFields.KeyCode.value
        1 -> FederalEntityFields.Name.value
        2 -> FederalEntityFields.CreatedAt.value
        3 -> FederalEntityFields.UpdatedAt.value
        else -> null
    }

    val columns = remember {
        fun onSort(index: Int, ascending: Boolean) {
            state.sortColumnIndex = index
            state.sortAscending = ascending
        }

        listOf(
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.Center,
                width = TableColumnWidth.MinIntrinsic,
                header = { Text(keyCodeColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                width = TableColumnWidth.Fraction(0.3f),
                header = { Text(nameColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.Center,
                width = TableColumnWidth.Fraction(0.18f),
                header = {
                    Text(
                        text = createdAtColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.Center,
                width = TableColumnWidth.Fraction(0.18f),
                header = {
                    Text(
                        text = updatedAtColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                alignment = Alignment.Center,
                width = TableColumnWidth.Fraction(0.18f),
                header = { Text(actionsColumnName) }
            )
        )
    }

    Surface(
        modifier = Modifier.then(modifier),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        PaginatedDataTable(
            modifier = Modifier.fillMaxWidth(),
            total = data.total,
            value = value,
            onValueChange = { search ->
                val orderBy = getOrderBy(state.sortColumnIndex)
                onSearch(search, state.pageIndex, state.pageSize, orderBy, state.sortAscending)
            },
            columns = columns,
            state = state,
            pageSizes = pageSizes,
            onSearch = { search, pageIndex, pageSize, sortBy, isAscending ->
                val orderBy = getOrderBy(sortBy)
                onSearch(search, pageIndex, pageSize, orderBy, isAscending)
            }
        ) {
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
                        Text(federalEntity.createdAt.toLocalDateTime().format(dateTimeFormatter))
                    }
                    cell {
                        Text(federalEntity.updatedAt?.toLocalDateTime()?.format(dateTimeFormatter) ?: "N/A")
                    }

                    cell {
                        Row {
                            PlainTextTooltip(
                                tooltip = {
                                    Text(stringResource(Res.string.edit))
                                }
                            ) {
                                IconButton(
                                    onClick = { onEditRequest(federalEntity) }
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
                                    onClick = { onDeleteRequest(federalEntity) }
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
}
