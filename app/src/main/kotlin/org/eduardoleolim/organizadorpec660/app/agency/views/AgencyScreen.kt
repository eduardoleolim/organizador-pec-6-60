package org.eduardoleolim.organizadorpec660.app.agency.views

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
import org.eduardoleolim.organizadorpec660.app.agency.model.AgencyScreenModel
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.agencies
import org.eduardoleolim.organizadorpec660.app.shared.composables.reset
import org.eduardoleolim.organizadorpec660.core.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.jetbrains.compose.resources.stringResource

class AgencyScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { AgencyScreenModel(queryBus, commandBus, Dispatchers.IO) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormModal by remember { mutableStateOf(false) }
        var selectedAgency by remember { mutableStateOf<AgencyResponse?>(null) }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var searchValue by remember { mutableStateOf("") }
        val resetScreen = remember {
            fun() {
                val offset = state.pageIndex * state.pageSize
                searchValue = ""
                state.reset(pageSizes.first())
                screenModel.searchAgencies(searchValue, null, state.pageSize, offset)
                showDeleteModal = false
                showFormModal = false
                selectedAgency = null
            }
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            AgencyScreenHeader(
                onSaveRequest = {
                    selectedAgency = null
                    showFormModal = true
                },
                onImportExportRequest = {}
            )

            AgenciesTable(
                modifier = Modifier.fillMaxSize(),
                value = searchValue,
                onValueChange = { searchValue = it },
                pageSizes = pageSizes,
                state = state,
                data = screenModel.agencies,
                onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                    val offset = pageIndex * pageSize
                    val orders = orderBy?.let {
                        val orderType = if (isAscending) "ASC" else "DESC"
                        arrayOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                    }

                    screenModel.searchAgencies(search, orders, pageSize, offset)
                },
                onDeleteRequest = { agency ->
                    selectedAgency = agency
                    showDeleteModal = true
                },
                onEditRequest = { agency ->
                    selectedAgency = agency
                    showFormModal = true
                }
            )

            when {
                showFormModal -> {
                    screenModel.resetForm()
                    AgencyFormModal(
                        screenModel = screenModel,
                        agency = selectedAgency,
                        onDismissRequest = { resetScreen() },
                        onSuccess = { resetScreen() }
                    )
                }

                showDeleteModal && selectedAgency != null -> {
                    screenModel.resetDeleteModal()
                    AgencyDeleteModal(
                        screenModel = screenModel,
                        agency = selectedAgency!!,
                        onSuccess = { resetScreen() },
                        onDismissRequest = { resetScreen() }
                    )
                }
            }
        }
    }

    @Composable
    fun AgencyScreenHeader(
        onSaveRequest: () -> Unit,
        onImportExportRequest: () -> Unit
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.agencies),
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
                    contentDescription = "Import/Export agencies"
                )
            }

            SmallFloatingActionButton(
                onClick = onSaveRequest,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add agency"
                )
            }
        }
    }
}
