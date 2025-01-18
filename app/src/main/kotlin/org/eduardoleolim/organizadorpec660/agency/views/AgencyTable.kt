/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.agency.views

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
import org.eduardoleolim.organizadorpec660.agency.application.AgenciesResponse
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyFields
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
                width = TableColumnWidth.Fraction(0.2f),
                header = { Text(nameColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.Center,
                width = TableColumnWidth.MinIntrinsic,
                header = { Text(consecutiveColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterStart,
                width = TableColumnWidth.Fraction(0.18f),
                header = { Text(municipalityColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.Center,
                width = TableColumnWidth.Fraction(0.16f),
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
                width = TableColumnWidth.Fraction(0.16f),
                header = {
                    Text(
                        text = updatedAtColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                alignment = Alignment.Center,
                width = TableColumnWidth.Fraction(0.2f),
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
            onValueChange = onValueChange,
            columns = columns,
            state = state,
            pageSizes = pageSizes,
            onSearch = { search, pageIndex, pageSize, sortBy, isAscending ->
                val orderBy = when (sortBy) {
                    0 -> AgencyFields.Name.value
                    1 -> AgencyFields.Consecutive.value
                    2 -> AgencyFields.MunicipalityKeyCode.value
                    3 -> AgencyFields.CreatedAt.value
                    4 -> AgencyFields.UpdatedAt.value
                    else -> null
                }
                onSearch(search, pageIndex, pageSize, orderBy, isAscending)
            }
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
                        Row {
                            PlainTextTooltip(
                                tooltip = {
                                    Text(stringResource(Res.string.edit))
                                }
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
                                tooltip = {
                                    Text(stringResource(Res.string.delete))
                                }
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
}
