package org.eduardoleolim.organizadorpec660.app.instrument.views

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
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.instrument.model.InstrumentScreenModel
import org.eduardoleolim.organizadorpec660.app.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.app.shared.composables.PlainTextTooltip
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortAscending
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortColumnIndex
import org.eduardoleolim.organizadorpec660.core.instrument.application.InstrumentResponse
import org.eduardoleolim.organizadorpec660.core.instrument.application.InstrumentsResponse
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentFields
import org.jetbrains.compose.resources.stringResource
import java.text.DateFormatSymbols

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
    onChangeStateRequest: (InstrumentResponse, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    pageIndex,
                    pageSize,
                    orderBy,
                    isAscending
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            data.instruments.forEach { instrument ->
                val statisticType = instrument.statisticType
                val federalEntity = instrument.federalEntity
                val municipality = instrument.municipality

                row {
                    cell {
                        Checkbox(
                            checked = instrument.savedInSIRESO,
                            onCheckedChange = { onChangeStateRequest(instrument, it) }
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
