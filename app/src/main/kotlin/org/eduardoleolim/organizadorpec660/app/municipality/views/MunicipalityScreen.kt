package org.eduardoleolim.organizadorpec660.app.municipality.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.municipalities
import org.eduardoleolim.organizadorpec660.app.municipality.model.MunicipalityScreenModel
import org.eduardoleolim.organizadorpec660.app.shared.composables.reset
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.jetbrains.compose.resources.stringResource

class MunicipalityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { MunicipalityScreenModel(queryBus, commandBus, Dispatchers.IO) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormModal by remember { mutableStateOf(false) }
        var selectedFederalEntityId by remember { mutableStateOf<String?>(null) }
        var selectedMunicipality by remember { mutableStateOf<MunicipalityResponse?>(null) }
        var searchValue by remember { mutableStateOf("") }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        val resetScreen = remember {
            fun() {
                val offset = state.pageIndex * state.pageSize
                searchValue = ""
                state.reset(pageSizes.first())
                screenModel.searchMunicipalities(searchValue, selectedFederalEntityId, null, state.pageSize, offset)
                screenModel.searchAllFederalEntities()
                showDeleteModal = false
                showFormModal = false
                selectedMunicipality = null
            }
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            MunicipalityScreenHeader(
                onSaveRequest = {
                    selectedMunicipality = null
                    showFormModal = true
                },
                onImportExportRequest = { }
            )

            MunicipalitiesTable(
                modifier = Modifier.fillMaxSize(),
                screenModel = screenModel,
                value = searchValue,
                onValueChange = { searchValue = it },
                pageSizes = pageSizes,
                data = screenModel.municipalities,
                state = state,
                onSearch = { search, federalEntityId, pageIndex, pageSize, orderBy, isAscending ->
                    selectedFederalEntityId = federalEntityId
                    val orders = orderBy?.let {
                        val orderType = if (isAscending) "ASC" else "DESC"
                        listOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                    }

                    screenModel.searchMunicipalities(search, federalEntityId, orders, pageSize, pageIndex * pageSize)
                },
                onDeleteRequest = { federalEntity ->
                    selectedMunicipality = federalEntity
                    showDeleteModal = true
                },
                onEditRequest = { federalEntity ->
                    selectedMunicipality = federalEntity
                    showFormModal = true
                }
            )

            when {
                showFormModal -> {
                    screenModel.resetForm()
                    MunicipalityFormModal(
                        screenModel = screenModel,
                        municipality = selectedMunicipality,
                        onDismissRequest = { resetScreen() },
                        onSuccess = { resetScreen() }
                    )
                }

                showDeleteModal && selectedMunicipality != null -> {
                    screenModel.resetDeleteModal()
                    MunicipalityDeleteModal(
                        screenModel = screenModel,
                        municipality = selectedMunicipality!!,
                        onSuccess = { resetScreen() },
                        onFail = { resetScreen() },
                        onDismissRequest = { resetScreen() }
                    )
                }
            }
        }
    }

    @Composable
    fun MunicipalityScreenHeader(
        onSaveRequest: () -> Unit,
        onImportExportRequest: () -> Unit
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.municipalities),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.weight(1.0f))

            SmallFloatingActionButton(
                onClick = onImportExportRequest,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.ImportExport,
                    contentDescription = "Import/Export municipalities"
                )
            }

            SmallFloatingActionButton(
                onClick = onSaveRequest,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add municipality"
                )
            }
        }
    }
}
