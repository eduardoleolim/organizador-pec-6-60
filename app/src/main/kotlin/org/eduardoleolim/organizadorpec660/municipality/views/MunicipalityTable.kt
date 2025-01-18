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

package org.eduardoleolim.organizadorpec660.municipality.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityFields
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
fun MunicipalityScreen.MunicipalitiesTable(
    value: String,
    onValueChange: (String) -> Unit,
    federalEntities: List<FederalEntityResponse>,
    federalEntity: FederalEntityResponse?,
    onFederalEntitySelected: (FederalEntityResponse?) -> Unit,
    pageSizes: List<Int>,
    data: MunicipalitiesResponse,
    state: PaginatedDataTableState,
    onSearch: (search: String, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
    onDeleteRequest: (MunicipalityResponse) -> Unit,
    onEditRequest: (MunicipalityResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyCodeColumnName = stringResource(Res.string.mun_keycode)
    val nameColumnName = stringResource(Res.string.mun_name)
    val federalEntityColumnName = stringResource(Res.string.mun_federal_entity)
    val createdAtColumnName = stringResource(Res.string.mun_created_at)
    val updatedAtColumnName = stringResource(Res.string.mun_updated_at)
    val actionsColumnName = stringResource(Res.string.table_col_actions)

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
                header = { Text(nameColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                width = TableColumnWidth.Fraction(0.2f),
                header = {
                    Text(
                        text = federalEntityColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.Center,
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
                alignment = Alignment.Center,
                width = TableColumnWidth.Fraction(0.2f),
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
                val orderBy = when (sortBy) {
                    0 -> MunicipalityFields.KeyCode.value
                    1 -> MunicipalityFields.Name.value
                    2 -> MunicipalityFields.FederalEntityKeyCode.value
                    3 -> MunicipalityFields.CreatedAt.value
                    4 -> MunicipalityFields.UpdatedAt.value
                    else -> null
                }
                onSearch(search, pageIndex, pageSize, orderBy, isAscending)
            },
            header = {
                SelectFederalEntity(
                    federalEntities = federalEntities,
                    federalEntity = federalEntity,
                    onFederalEntitySelected = { federalEntity ->
                        state.pageIndex = 0
                        onFederalEntitySelected(federalEntity)
                    }
                )
            }
        ) {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            data.municipalities.forEach { municipality ->
                row {
                    cell {
                        Text(municipality.keyCode)
                    }
                    cell {
                        Text(municipality.name)
                    }
                    cell {
                        Text(municipality.federalEntity.name)
                    }

                    cell {
                        Text(municipality.createdAt.toLocalDateTime().format(dateTimeFormatter))
                    }
                    cell {
                        Text(municipality.updatedAt?.toLocalDateTime()?.format(dateTimeFormatter) ?: "N/A")
                    }

                    cell {
                        Row {
                            PlainTextTooltip(
                                tooltip = {
                                    Text(stringResource(Res.string.edit))
                                }
                            ) {
                                IconButton(
                                    onClick = { onEditRequest(municipality) }
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
                                    onClick = { onDeleteRequest(municipality) }
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

@Composable
private fun SelectFederalEntity(
    federalEntities: List<FederalEntityResponse>,
    federalEntity: FederalEntityResponse?,
    onFederalEntitySelected: (FederalEntityResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = federalEntity?.let { "${it.keyCode} - ${it.name}" }
                    ?: stringResource(Res.string.mun_form_select_all),
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Expand"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(0.dp, 300.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.mun_form_select_all),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    expanded = false
                    onFederalEntitySelected(null)
                }
            )

            federalEntities.forEach { federalEntity ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${federalEntity.keyCode} - ${federalEntity.name}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        onFederalEntitySelected(federalEntity)
                    }
                )
            }
        }
    }
}

