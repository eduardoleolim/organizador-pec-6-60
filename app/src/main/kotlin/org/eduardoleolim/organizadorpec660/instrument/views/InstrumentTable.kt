package org.eduardoleolim.organizadorpec660.instrument.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.instrument.application.InstrumentResponse
import org.eduardoleolim.organizadorpec660.instrument.application.InstrumentsResponse
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentFields
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.shared.composables.PlainTextTooltip
import org.eduardoleolim.organizadorpec660.shared.composables.sortAscending
import org.eduardoleolim.organizadorpec660.shared.composables.sortColumnIndex
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InstrumentScreen.InstrumentsTable(
    value: String,
    onValueChange: (String) -> Unit,
    statisticYears: List<Int>,
    statisticYear: Int?,
    onStatisticYearSelected: (Int?) -> Unit,
    statisticMonths: List<Pair<Int, String>>,
    statisticMonth: Pair<Int, String>?,
    onStatisticMonthSelected: (Pair<Int, String>?) -> Unit,
    statisticTypes: List<StatisticTypeResponse>,
    statisticType: StatisticTypeResponse?,
    onStatisticTypeSelected: (StatisticTypeResponse?) -> Unit,
    federalEntities: List<FederalEntityResponse>,
    federalEntity: FederalEntityResponse?,
    onFederalEntitySelected: (FederalEntityResponse?) -> Unit,
    municipalities: List<MunicipalityResponse>,
    municipality: MunicipalityResponse?,
    onMunicipalitySelected: (MunicipalityResponse?) -> Unit,
    agencies: List<AgencyResponse>,
    agency: AgencyResponse?,
    onAgencySelected: (AgencyResponse?) -> Unit,
    pageSizes: List<Int>,
    data: InstrumentsResponse,
    state: PaginatedDataTableState,
    onSearch: (search: String, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
    onDeleteRequest: (InstrumentResponse) -> Unit,
    onEditRequest: (InstrumentResponse) -> Unit,
    onCopyRequest: (InstrumentResponse) -> Unit,
    onShowDetailsRequest: (InstrumentResponse) -> Unit,
    onChangeSiresoStatusRequest: (InstrumentResponse, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val siresoColumnName = stringResource(Res.string.inst_in_sireso)
    val yearColumnName = stringResource(Res.string.inst_year)
    val monthColumnName = stringResource(Res.string.inst_month)
    val statisticTypeColumnName = stringResource(Res.string.inst_statistic_type)
    val federalEntityColumnName = stringResource(Res.string.inst_federal_entity)
    val municipalityColumnName = stringResource(Res.string.inst_municipality)
    val actionsColumnName = stringResource(Res.string.table_col_actions)

    val columns = remember {
        fun onSort(index: Int, ascending: Boolean) {
            state.sortColumnIndex = index
            state.sortAscending = ascending
        }

        listOf(
            DataColumn(
                alignment = Alignment.Center,
                width = TableColumnWidth.Fixed(140.dp),
                header = {
                    Text(
                        text = siresoColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.Center,
                width = TableColumnWidth.MinIntrinsic,
                header = {
                    Text(
                        text = yearColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterStart,
                width = TableColumnWidth.MinIntrinsic,
                header = {
                    Text(
                        text = monthColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterStart,
                width = TableColumnWidth.Fraction(0.18f),
                header = {
                    Text(
                        text = statisticTypeColumnName,
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterStart,
                width = TableColumnWidth.Fixed(180.dp),
                header = { Text(federalEntityColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterStart,
                width = TableColumnWidth.Fixed(180.dp),
                header = { Text(municipalityColumnName) }
            ),
            DataColumn(
                alignment = Alignment.Center,
                width = TableColumnWidth.MinIntrinsic,
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
            total = data.total,
            value = value,
            onValueChange = onValueChange,
            columns = columns,
            state = state,
            pageSizes = pageSizes,
            onSearch = { search, pageIndex, pageSize, sortBy, isAscending ->
                val orderBy = when (sortBy) {
                    1 -> InstrumentFields.StatisticYear.value
                    2 -> InstrumentFields.StatisticMonth.value
                    3 -> InstrumentFields.StatisticTypeKeyCode.value
                    4 -> InstrumentFields.FederalEntityKeyCode.value
                    5 -> InstrumentFields.MunicipalityKeyCode.value
                    else -> null
                }

                onSearch(search, pageIndex, pageSize, orderBy, isAscending)
            },
            header = {
                SelectStatisticYear(
                    statisticYears = statisticYears,
                    statisticYear = statisticYear,
                    onStatisticYearSelected = {
                        state.pageIndex = 0
                        onStatisticYearSelected(it)
                    }
                )

                SelectStatisticMonth(
                    statisticMonth = statisticMonth,
                    statisticMonths = statisticMonths,
                    onStatisticMonthSelected = {
                        state.pageIndex = 0
                        onStatisticMonthSelected(it)
                    }
                )

                SelectStatisticType(
                    statisticType = statisticType,
                    statisticTypes = statisticTypes,
                    onStatisticTypeSelected = {
                        state.pageIndex = 0
                        onStatisticTypeSelected(it)
                    }
                )

                SelectFederalEntity(
                    federalEntities = federalEntities,
                    federalEntity = federalEntity,
                    onFederalEntitySelected = {
                        state.pageIndex = 0
                        onFederalEntitySelected(it)
                    }
                )

                SelectMunicipality(
                    municipalities = municipalities,
                    municipality = municipality,
                    onMunicipalitySelected = {
                        state.pageIndex = 0
                        onMunicipalitySelected(it)
                    }
                )

                SelectAgency(
                    agencies = agencies,
                    agency = agency,
                    onAgencySelected = {
                        state.pageIndex = 0
                        onAgencySelected(it)
                    }
                )
            }
        ) {
            data.instruments.forEach { instrument ->
                row {
                    cell {
                        Checkbox(
                            checked = instrument.savedInSIRESO,
                            onCheckedChange = {
                                onChangeSiresoStatusRequest(instrument, it)

                                val orderBy = when (state.sortColumnIndex) {
                                    1 -> InstrumentFields.StatisticYear.value
                                    2 -> InstrumentFields.StatisticMonth.value
                                    3 -> InstrumentFields.StatisticTypeKeyCode.value
                                    4 -> InstrumentFields.FederalEntityKeyCode.value
                                    5 -> InstrumentFields.MunicipalityKeyCode.value
                                    else -> null
                                }

                                onSearch(value, state.pageIndex, state.pageSize, orderBy, state.sortAscending)
                            }
                        )
                    }

                    cell {
                        Text("${instrument.statisticYear}")
                    }

                    cell {
                        Text("${statisticMonths.toMap()[instrument.statisticMonth]}")
                    }

                    cell {
                        with(instrument.statisticType) {
                            Text("$keyCode - $name")
                        }
                    }

                    cell {
                        with(instrument.federalEntity) {
                            Text("$keyCode - $name")
                        }
                    }

                    cell {
                        with(instrument.municipality) {
                            Text("$keyCode - $name")
                        }
                    }

                    cell {
                        Row {
                            PlainTextTooltip(
                                tooltip = {
                                    Text(stringResource(Res.string.inst_show_details))
                                }
                            ) {
                                IconButton(
                                    onClick = { onShowDetailsRequest(instrument) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Show details"
                                    )
                                }
                            }

                            PlainTextTooltip(
                                tooltip = {
                                    Text(stringResource(Res.string.inst_copy_document))
                                }
                            ) {
                                IconButton(
                                    onClick = { onCopyRequest(instrument) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FileCopy,
                                        contentDescription = "Copy instrument file"
                                    )
                                }
                            }

                            PlainTextTooltip(
                                tooltip = {
                                    Text(stringResource(Res.string.edit))
                                }
                            ) {
                                IconButton(
                                    onClick = { onEditRequest(instrument) }
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
                                    onClick = { onDeleteRequest(instrument) }
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
private fun SelectStatisticYear(
    statisticYears: List<Int>,
    statisticYear: Int?,
    onStatisticYearSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = statisticYear?.toString() ?: stringResource(Res.string.inst_select_all_years),
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
                        text = stringResource(Res.string.inst_select_all_years),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    expanded = false
                    onStatisticYearSelected(null)
                }
            )

            statisticYears.forEach { year ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = year.toString(),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        onStatisticYearSelected(year)
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectStatisticMonth(
    statisticMonths: List<Pair<Int, String>>,
    statisticMonth: Pair<Int, String>?,
    onStatisticMonthSelected: (Pair<Int, String>?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = statisticMonth?.second ?: stringResource(Res.string.inst_select_all_months),
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
                        text = stringResource(Res.string.inst_select_all_months),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    expanded = false
                    onStatisticMonthSelected(null)
                }
            )

            statisticMonths.forEach { month ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = month.second,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        onStatisticMonthSelected(month)
                    }
                )
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
                    ?: stringResource(Res.string.inst_select_all_federal_entities),
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
                        text = stringResource(Res.string.inst_select_all_federal_entities),
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

@Composable
private fun SelectMunicipality(
    municipalities: List<MunicipalityResponse>,
    municipality: MunicipalityResponse?,
    onMunicipalitySelected: (MunicipalityResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = municipality?.let { "${it.keyCode} - ${it.name}" }
                    ?: stringResource(Res.string.inst_select_all_municipalities),
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
                        text = stringResource(Res.string.inst_select_all_municipalities),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    expanded = false
                    onMunicipalitySelected(null)
                }
            )

            municipalities.forEach { municipality ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${municipality.keyCode} - ${municipality.name}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        onMunicipalitySelected(municipality)
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectAgency(
    agencies: List<AgencyResponse>,
    agency: AgencyResponse?,
    onAgencySelected: (AgencyResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = agency?.let { "${it.consecutive} - ${it.name}" }
                    ?: stringResource(Res.string.inst_select_all_agencies),
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
                        text = stringResource(Res.string.inst_select_all_agencies),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    expanded = false
                    onAgencySelected(null)
                }
            )

            agencies.forEach { agency ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${agency.consecutive} - ${agency.name}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        onAgencySelected(agency)
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectStatisticType(
    statisticType: StatisticTypeResponse?,
    statisticTypes: List<StatisticTypeResponse>,
    onStatisticTypeSelected: (StatisticTypeResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = statisticType?.let { "${it.keyCode} - ${it.name}" }
                    ?: stringResource(Res.string.inst_select_all_statistic_types),
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
            modifier = Modifier.heightIn(0.dp, 300.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.inst_select_all_statistic_types),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    expanded = false
                    onStatisticTypeSelected(null)
                }
            )

            statisticTypes.forEach { statisticType ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${statisticType.keyCode} - ${statisticType.name}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        onStatisticTypeSelected(statisticType)
                    }
                )
            }
        }
    }
}
