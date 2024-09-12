package org.eduardoleolim.organizadorpec660.instrument.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.instrument.model.InstrumentScreenModel
import org.eduardoleolim.organizadorpec660.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.shared.composables.PlainTextTooltip
import org.eduardoleolim.organizadorpec660.shared.composables.sortAscending
import org.eduardoleolim.organizadorpec660.shared.composables.sortColumnIndex
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.instrument.application.InstrumentResponse
import org.eduardoleolim.organizadorpec660.instrument.application.InstrumentsResponse
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentFields
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.jetbrains.compose.resources.stringResource
import java.text.DateFormatSymbols
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InstrumentScreen.InstrumentsTable(
    value: String,
    onValueChange: (String) -> Unit,
    screenModel: InstrumentScreenModel,
    pageSizes: List<Int>,
    data: InstrumentsResponse,
    state: PaginatedDataTableState,
    onSearch: (search: String, federalEntityId: String?, municipalityId: String?, agencyId: String?, statisticTypeId: String?, statisticYear: Int?, statisticMonth: Int?, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
    onDeleteRequest: (InstrumentResponse) -> Unit,
    onEditRequest: (InstrumentResponse) -> Unit,
    onCopyRequest: (InstrumentResponse) -> Unit,
    onShowDetailsRequest: (InstrumentResponse) -> Unit,
    onChangeStateRequest: (InstrumentResponse, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val years = remember { (LocalDate.now().year downTo 1983).toList() }
    val months = remember {
        DateFormatSymbols().months.take(12).mapIndexed { index, month ->
            index + 1 to month.uppercase()
        }.toMap()
    }
    val siresoColumnName = stringResource(Res.string.inst_in_sireso)
    val yearColumnName = stringResource(Res.string.inst_year)
    val monthColumnName = stringResource(Res.string.inst_month)
    val statisticTypeColumnName = stringResource(Res.string.inst_statistic_type)
    val federalEntityColumnName = stringResource(Res.string.inst_federal_entity)
    val municipalityColumnName = stringResource(Res.string.inst_municipality)
    val actionsColumnName = stringResource(Res.string.table_col_actions)

    var statisticYear by remember { mutableStateOf<Int?>(null) }
    var statisticMonth by remember { mutableStateOf<Int?>(null) }
    var statisticTypeId by remember { mutableStateOf<String?>(null) }
    var federalEntityId by remember { mutableStateOf<String?>(null) }
    var municipalityId by remember { mutableStateOf<String?>(null) }
    var agencyId by remember { mutableStateOf<String?>(null) }

    val columns = remember {
        fun onSort(index: Int, ascending: Boolean) {
            state.sortColumnIndex = index
            state.sortAscending = ascending
        }

        listOf(
            DataColumn(
                alignment = Alignment.CenterHorizontally,
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
                alignment = Alignment.CenterHorizontally,
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
                alignment = Alignment.Start,
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
                alignment = Alignment.Start,
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
                alignment = Alignment.Start,
                width = TableColumnWidth.Fixed(180.dp),
                header = { Text(federalEntityColumnName) }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.Start,
                width = TableColumnWidth.Fixed(180.dp),
                header = { Text(municipalityColumnName) }
            ),
            DataColumn(
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.MinIntrinsic,
                header = { Text(actionsColumnName) }
            )
        )
    }

    LaunchedEffect(Unit) {
        screenModel.searchAllFederalEntities()
        screenModel.searchAllStatisticTypes()
    }

    LaunchedEffect(federalEntityId) {
        screenModel.searchMunicipalities(federalEntityId)
        municipalityId = null
    }

    LaunchedEffect(municipalityId) {
        screenModel.searchAgencies(municipalityId)
        agencyId = null
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

                onSearch(
                    search,
                    federalEntityId,
                    municipalityId,
                    agencyId,
                    statisticTypeId,
                    statisticYear,
                    statisticMonth,
                    pageIndex,
                    pageSize,
                    orderBy,
                    isAscending
                )
            },
            header = {
                SelectStatisticYear(
                    statisticYear = statisticYear,
                    years = years,
                    onYearSelected = {
                        statisticYear = it
                        state.pageIndex = -1
                    }
                )

                SelectStatisticMonth(
                    statisticMonth = statisticMonth,
                    months = months.toList(),
                    onMonthSelected = {
                        statisticMonth = it?.first
                        state.pageIndex = -1
                    }
                )

                SelectStatisticType(
                    statisticTypeId = statisticTypeId,
                    statisticTypes = screenModel.statisticTypes,
                    onStatisticTypeSelected = {
                        statisticTypeId = it?.id
                        state.pageIndex = -1
                    }
                )

                SelectFederalEntity(
                    federalEntityId = federalEntityId,
                    federalEntities = screenModel.federalEntities,
                    onFederalEntitySelected = {
                        federalEntityId = it?.id
                        state.pageIndex = -1
                    }
                )

                SelectMunicipality(
                    municipalityId = municipalityId,
                    municipalities = screenModel.municipalities,
                    onMunicipalitySelected = {
                        municipalityId = it?.id
                        state.pageIndex = -1
                    }
                )

                SelectAgency(
                    agencyId = agencyId,
                    agencies = screenModel.agencies,
                    onAgencySelected = {
                        agencyId = it?.id
                        state.pageIndex = -1
                    }
                )
            }
        ) {
            data.instruments.forEach { instrument ->
                val statisticType = instrument.statisticType
                val federalEntity = instrument.federalEntity
                val municipality = instrument.municipality

                row {
                    cell {
                        Checkbox(
                            checked = instrument.savedInSIRESO,
                            onCheckedChange = {
                                onChangeStateRequest(instrument, it)

                                val orderBy = when (state.sortColumnIndex) {
                                    1 -> InstrumentFields.StatisticYear.value
                                    2 -> InstrumentFields.StatisticMonth.value
                                    3 -> InstrumentFields.StatisticTypeKeyCode.value
                                    4 -> InstrumentFields.FederalEntityKeyCode.value
                                    5 -> InstrumentFields.MunicipalityKeyCode.value
                                    else -> null
                                }

                                onSearch(
                                    value,
                                    federalEntityId,
                                    municipalityId,
                                    agencyId,
                                    statisticTypeId,
                                    statisticYear,
                                    statisticMonth,
                                    state.pageIndex,
                                    state.pageSize,
                                    orderBy,
                                    state.sortAscending
                                )
                            }
                        )
                    }

                    cell {
                        Text("${instrument.statisticYear}")
                    }

                    cell {
                        Text("${months[instrument.statisticMonth]}")
                    }

                    cell {
                        Text("${statisticType.keyCode} - ${statisticType.name}")
                    }

                    cell {
                        Text("${federalEntity.keyCode} - ${federalEntity.name}")
                    }

                    cell {
                        Text("${municipality.keyCode} - ${municipality.name}")
                    }

                    cell {
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

@Composable
private fun SelectStatisticYear(statisticYear: Int?, years: List<Int>, onYearSelected: (Int?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedYear by remember(statisticYear) { mutableStateOf(statisticYear) }

    LaunchedEffect(selectedYear) {
        onYearSelected(selectedYear)
    }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = selectedYear?.toString() ?: stringResource(Res.string.inst_select_all_years),
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
                        text = stringResource(Res.string.inst_select_all_years),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    expanded = false
                    selectedYear = null
                }
            )

            years.forEach { year ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = year.toString(),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        selectedYear = year
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectStatisticMonth(
    statisticMonth: Int?,
    months: List<Pair<Int, String>>,
    onMonthSelected: (Pair<Int, String>?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMonth by remember(statisticMonth) { mutableStateOf(months.firstOrNull { it.first == statisticMonth }) }

    LaunchedEffect(selectedMonth) {
        onMonthSelected(selectedMonth)
    }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = selectedMonth?.second ?: stringResource(Res.string.inst_select_all_months),
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
                        text = stringResource(Res.string.inst_select_all_months),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    expanded = false
                    selectedMonth = null
                }
            )

            months.forEach { month ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = month.second,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        selectedMonth = month
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectFederalEntity(
    federalEntityId: String?,
    federalEntities: List<FederalEntityResponse>,
    onFederalEntitySelected: (FederalEntityResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedFederalEntity by remember(federalEntityId, federalEntities) {
        mutableStateOf(federalEntities.firstOrNull { it.id == federalEntityId })
    }

    LaunchedEffect(selectedFederalEntity) {
        onFederalEntitySelected(selectedFederalEntity)
    }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = selectedFederalEntity?.let { "${it.keyCode} - ${it.name}" }
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
            modifier = Modifier.heightIn(0.dp, 300.dp)
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
                    selectedFederalEntity = null
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
                        selectedFederalEntity = federalEntity
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectMunicipality(
    municipalityId: String?,
    municipalities: List<MunicipalityResponse>,
    onMunicipalitySelected: (MunicipalityResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMunicipality by remember(municipalityId, municipalities) {
        mutableStateOf(municipalities.firstOrNull { it.id == municipalityId })
    }

    LaunchedEffect(selectedMunicipality) {
        onMunicipalitySelected(selectedMunicipality)
    }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = selectedMunicipality?.let { "${it.keyCode} - ${it.name}" }
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
            modifier = Modifier.heightIn(0.dp, 300.dp)
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
                    selectedMunicipality = null
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
                        selectedMunicipality = municipality
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectAgency(
    agencyId: String?,
    agencies: List<AgencyResponse>,
    onAgencySelected: (AgencyResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedAgency by remember(agencyId, agencies) {
        mutableStateOf(agencies.firstOrNull { it.id == agencyId })
    }

    LaunchedEffect(selectedAgency) {
        onAgencySelected(selectedAgency)
    }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = selectedAgency?.let { "${it.consecutive} - ${it.name}" }
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
            modifier = Modifier.heightIn(0.dp, 300.dp)
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
                    selectedAgency = null
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
                        selectedAgency = agency
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectStatisticType(
    statisticTypeId: String?,
    statisticTypes: List<StatisticTypeResponse>,
    onStatisticTypeSelected: (StatisticTypeResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStatisticType by remember(statisticTypeId, statisticTypes) {
        mutableStateOf(statisticTypes.firstOrNull { it.id == statisticTypeId })
    }

    LaunchedEffect(selectedStatisticType) {
        onStatisticTypeSelected(selectedStatisticType)
    }

    Box {
        TextButton(
            onClick = { expanded = true },
        ) {
            Text(
                text = selectedStatisticType?.let { "${it.keyCode} - ${it.name}" }
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
                    selectedStatisticType = null
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
                        selectedStatisticType = statisticType
                    }
                )
            }
        }
    }
}
