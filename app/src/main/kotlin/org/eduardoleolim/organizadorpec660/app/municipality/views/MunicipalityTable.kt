package org.eduardoleolim.organizadorpec660.app.municipality.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import org.eduardoleolim.organizadorpec660.app.municipality.model.MunicipalityScreenModel
import org.eduardoleolim.organizadorpec660.app.shared.composables.PaginatedDataTable
import org.eduardoleolim.organizadorpec660.app.shared.composables.PlainTextTooltip
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortAscending
import org.eduardoleolim.organizadorpec660.app.shared.composables.sortColumnIndex
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
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
    onEditRequest: (MunicipalityResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val orders = listOf("keyCode", "name", "federalEntity.name", "createdAt", "updatedAt")
    var federalEntityId by remember { mutableStateOf<String?>(null) }

    val columns = remember {
        fun onSort(index: Int, ascending: Boolean) {
            state.sortColumnIndex = index
            state.sortAscending = ascending
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
                onSearch(search, federalEntityId, pageIndex, pageSize, sortBy?.let { orders[it] }, isAscending)
            },
            header = {
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    var selectedFederalEntity by remember { mutableStateOf<FederalEntityResponse?>(null) }

                    LaunchedEffect(selectedFederalEntity) {
                        federalEntityId = selectedFederalEntity?.id
                        state.pageIndex = -1
                    }

                    LaunchedEffect(Unit) {
                        screenModel.searchAllFederalEntities()
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

                        screenModel.federalEntities.value.forEach { federalEntity ->
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
                        PlainTextTooltip(
                            tooltip = { Text("Editar") }
                        ) {
                            IconButton(
                                onClick = { onEditRequest(municipality) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar"
                                )
                            }
                        }

                        PlainTextTooltip(
                            tooltip = { Text("Eliminar") }
                        ) {
                            IconButton(
                                onClick = { onDeleteRequest(municipality) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
