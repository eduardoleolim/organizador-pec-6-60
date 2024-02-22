package org.eduardoleolim.organizadorPec660.app.municipality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import org.eduardoleolim.organizadorPec660.app.shared.components.PaginatedDataTable
import org.eduardoleolim.organizadorPec660.app.shared.components.tooltipOnHover
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorPec660.core.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorPec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorPec660.core.shared.domain.toLocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunicipalityScreen.MunicipalitiesTable(
    screenModel: MunicipalityScreenModel,
    value: String,
    onValueChange: (String) -> Unit,
    pageSizes: List<Int>,
    data: MunicipalitiesResponse,
    state: PaginatedDataTableState,
    onSearch: (search: String, federalEntityId: String?, pageIndex: Int, pageSize: Int, orderBy: String?, isAscending: Boolean) -> Unit,
    onDeleteRequest: (MunicipalityResponse) -> Unit,
    onEditRequest: (MunicipalityResponse) -> Unit
) {
    var sortColumnIndex by remember { mutableStateOf<Int?>(null) }
    var sortAscending by remember { mutableStateOf(true) }
    val orders = listOf("keyCode", "name", "federalEntity.name", "createdAt", "updatedAt")
    var federalEntityId by remember { mutableStateOf<String?>(null) }

    val columns = remember(sortColumnIndex, sortAscending) {
        fun onSort(index: Int, ascending: Boolean) {
            sortColumnIndex = index
            sortAscending = ascending
        }

        listOf(
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.MinIntrinsic
            ) {
                Text("Clave")
            },
            DataColumn(
                onSort = ::onSort,
            ) {
                Text("Nombre")
            },
            DataColumn(
                onSort = ::onSort
            ) {
                Text(
                    text = "Entidad federativa",
                    textAlign = TextAlign.Center
                )
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.18f)
            ) {
                Text(
                    text = "Fecha de registro",
                    textAlign = TextAlign.Center
                )
            },
            DataColumn(
                onSort = ::onSort,
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.18f)
            ) {
                Text(
                    text = "Última actualización",
                    textAlign = TextAlign.Center
                )
            },
            DataColumn(
                alignment = Alignment.CenterHorizontally,
                width = TableColumnWidth.Fraction(0.2f)
            ) {
                Text("Acciones")
            }
        )
    }

    PaginatedDataTable(
        modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        columns = columns,
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        state = state,
        pageSizes = pageSizes,
        onSearch = { search, pageIndex, pageSize, sortBy, isAscending ->
            onSearch(search, federalEntityId, pageIndex, pageSize, sortBy?.let { orders[it] }, isAscending)
        },
        header = {
            Box {
                var expanded by remember { mutableStateOf(false) }
                val federalEntities = remember { mutableListOf<FederalEntityResponse>() }
                var selectedFederalEntity by remember { mutableStateOf<FederalEntityResponse?>(null) }

                LaunchedEffect(selectedFederalEntity) {
                    federalEntityId = selectedFederalEntity?.id
                    state.pageIndex = -1
                }

                LaunchedEffect(Unit) {
                    screenModel.allFederalEntities { result ->
                        result.fold(
                            onSuccess = {
                                federalEntities.clear()
                                federalEntities.addAll(it)
                            },
                            onFailure = {
                                println(it.localizedMessage)
                                federalEntities.clear()
                            }
                        )
                    }
                }

                TextButton(
                    onClick = { expanded = true },
                ) {
                    Text(
                        text = selectedFederalEntity?.let { "${it.keyCode} - ${it.name}" } ?: "Todas las entidades",
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
                            Text(text = "Todas las entidades", color = MaterialTheme.colorScheme.onSurface)
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
    ) {
        val offset = data.offset ?: 0
        val filteredRecords = data.filtered
        val totalRecords = data.total
        val remainingRows = (totalRecords - offset) - filteredRecords

        // Add necessary rows for pagination
        repeat(offset) {
            row {
                // Necessary to avoid an index out of bounds exception
                // It is an issue with the library used to create the table
                cell { }
            }
        }

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
                    val editTooltipState = remember { PlainTooltipState() }
                    val deleteTooltipState = remember { PlainTooltipState() }

                    PlainTooltipBox(
                        tooltip = { Text("Editar") },
                        tooltipState = editTooltipState,
                    ) {
                        IconButton(
                            modifier = Modifier.tooltipOnHover(editTooltipState),
                            onClick = { onEditRequest(municipality) }
                        ) {
                            Icon(Icons.Default.Edit, "Editar")
                        }
                    }

                    PlainTooltipBox(
                        tooltip = { Text("Eliminar") },
                        tooltipState = deleteTooltipState
                    ) {
                        IconButton(
                            modifier = Modifier.tooltipOnHover(deleteTooltipState),
                            onClick = { onDeleteRequest(municipality) }
                        ) {
                            Icon(Icons.Default.Delete, "Eliminar")
                        }
                    }
                }
            }
        }

        repeat(remainingRows) {
            row {
                // Necessary to avoid an index out of bounds exception
                // It is an issue with the library used to create the table
                cell { }
            }
        }
    }
}
