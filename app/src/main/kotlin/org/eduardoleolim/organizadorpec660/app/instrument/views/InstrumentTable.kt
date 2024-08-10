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
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.delete
import org.eduardoleolim.organizadorpec660.app.generated.resources.edit
import org.eduardoleolim.organizadorpec660.app.generated.resources.table_col_actions
import org.eduardoleolim.organizadorpec660.app.instrument.model.InstrumentScreenModel
import org.eduardoleolim.organizadorpec660.app.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.app.shared.composables.PlainTextTooltip
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortAscending
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortColumnIndex
import org.eduardoleolim.organizadorpec660.core.instrument.application.InstrumentResponse
import org.eduardoleolim.organizadorpec660.core.instrument.application.InstrumentsResponse
import org.jetbrains.compose.resources.stringResource
import java.text.DateFormatSymbols
import java.time.format.DateTimeFormatter

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
    modifier: Modifier = Modifier
) {
    val months = remember {
        DateFormatSymbols().months.take(12).mapIndexed { index, month ->
            index + 1 to month.uppercase()
        }.toMap()
    }
    val orders = remember { listOf("", "keyCode", "name", "createdAt", "updatedAt") }
    val actionsColumnName = stringResource(Res.string.table_col_actions)

    val columns = remember {
        fun onSort(index: Int, ascending: Boolean) {
            state.sortColumnIndex = index
            state.sortAscending = ascending
        }

        listOf(
            DataColumn(
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.MinIntrinsic,
                header = {
                    Text("¿En SIRESO?")
                }
            ),
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.MinIntrinsic,
                header = {
                    Text(
                        text = "Año",
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
                        text = "Mes",
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
                        text = "Tipo de estadística",
                        textAlign = TextAlign.Center
                    )
                }
            ),
            DataColumn(
                alignment = Alignment.Start,
                width = TableColumnWidth.Fraction(0.18f),
                header = {
                    Text("Entidad Federativa")
                }
            ),
            DataColumn(
                alignment = Alignment.Start,
                width = TableColumnWidth.Fraction(0.18f),
                header = {
                    Text("Municipio")
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
                    sortBy?.let { orders[it] },
                    isAscending
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            data.instruments.forEach { instrument ->
                val statisticType = instrument.statisticType
                val federalEntity = instrument.federalEntity
                val municipality = instrument.municipality

                row {
                    cell {
                        Checkbox(
                            checked = instrument.savedInSIRESO,
                            onCheckedChange = { save ->
                                if (save) {
                                    screenModel.updateInstrumentAsSavedInSIRESO(instrument.id)
                                } else {
                                    screenModel.updateInstrumentAsNotSavedInSIRESO(instrument.id)
                                }
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
