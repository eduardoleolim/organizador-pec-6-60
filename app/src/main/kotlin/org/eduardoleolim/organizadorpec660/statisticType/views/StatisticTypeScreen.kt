package org.eduardoleolim.organizadorpec660.statisticType.views

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
import org.eduardoleolim.organizadorpec660.app.generated.resources.statistic_types
import org.eduardoleolim.organizadorpec660.shared.composables.reset
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeScreenModel
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.jetbrains.compose.resources.stringResource

class StatisticTypeScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { StatisticTypeScreenModel(queryBus, commandBus, Dispatchers.IO) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormModal by remember { mutableStateOf(false) }
        var selectedStatisticType by remember { mutableStateOf<StatisticTypeResponse?>(null) }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var searchValue by remember { mutableStateOf("") }
        val resetScreen = remember {
            fun() {
                searchValue = ""
                state.reset(pageSizes.first())
                screenModel.searchStatisticTypes(searchValue, null, state.pageSize, state.pageIndex * state.pageSize)
                showDeleteModal = false
                showFormModal = false
                selectedStatisticType = null
            }
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            StatisticTypeScreenHeader(
                onSaveRequest = {
                    selectedStatisticType = null
                    showFormModal = true
                },
                onImportExportRequest = { }
            )

            StatisticTypeTable(
                modifier = Modifier.fillMaxSize(),
                value = searchValue,
                onValueChange = { searchValue = it },
                pageSizes = pageSizes,
                state = state,
                data = screenModel.statisticTypes,
                onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                    val orders = orderBy?.let {
                        val orderType = if (isAscending) "ASC" else "DESC"
                        arrayOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                    }

                    screenModel.searchStatisticTypes(search, orders, pageSize, pageIndex * pageSize)
                },
                onDeleteRequest = { statisticType ->
                    selectedStatisticType = statisticType
                    showDeleteModal = true
                },
                onEditRequest = { statisticType ->
                    selectedStatisticType = statisticType
                    showFormModal = true
                }
            )

            when {
                showFormModal -> {
                    screenModel.resetForm()
                    StatisticTypeFormModal(
                        screenModel = screenModel,
                        statisticType = selectedStatisticType,
                        onDismissRequest = { resetScreen() },
                        onSuccess = { resetScreen() }
                    )
                }

                showDeleteModal && selectedStatisticType != null -> {
                    screenModel.resetDeleteModal()
                    StatisticTypeDeleteModal(
                        screenModel = screenModel,
                        statisticType = selectedStatisticType!!,
                        onSuccess = { resetScreen() },
                        onFail = { resetScreen() },
                        onDismissRequest = { resetScreen() }
                    )
                }
            }
        }
    }

    @Composable
    private fun StatisticTypeScreenHeader(
        onSaveRequest: () -> Unit,
        onImportExportRequest: () -> Unit
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.statistic_types),
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
                    contentDescription = "Import/Export statistic types"
                )
            }

            SmallFloatingActionButton(
                onClick = onSaveRequest,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add statistic type"
                )
            }
        }
    }
}
