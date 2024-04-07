package org.eduardoleolim.organizadorpec660.app.statisticType.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import org.eduardoleolim.organizadorpec660.app.shared.composables.reset
import org.eduardoleolim.organizadorpec660.app.statisticType.model.StatisticTypeScreenModel
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypeResponse

class StatisticTypeScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { StatisticTypeScreenModel(queryBus, commandBus) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormModal by remember { mutableStateOf(false) }
        var selectedStatisticType by remember { mutableStateOf<StatisticTypeResponse?>(null) }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var searchValue by remember { mutableStateOf("") }

        fun resetView() {
            searchValue = ""
            state.reset(pageSizes.first())
            screenModel.searchStatisticTypes(searchValue, null, state.pageSize, state.pageIndex * state.pageSize)
            showDeleteModal = false
            showFormModal = false
            selectedStatisticType = null
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tipos de estadística",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.weight(1.0f)
                )

                SmallFloatingActionButton(
                    onClick = {
                        selectedStatisticType = null
                        showFormModal = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar tipo de estadística"
                    )
                }
            }

            StatisticTypeTable(
                modifier = Modifier.fillMaxSize(),
                value = searchValue,
                onValueChange = { searchValue = it },
                pageSizes = pageSizes,
                state = state,
                data = screenModel.statisticTypes.value,
                onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                    val orders = orderBy?.let {
                        val orderType = if (isAscending) "ASC" else "DESC"
                        arrayOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                    }

                    screenModel.searchStatisticTypes(search, orders, pageSize, pageIndex * pageSize)
                },
                onDeleteRequest = { federalEntity ->
                    selectedStatisticType = federalEntity
                    showDeleteModal = true
                },
                onEditRequest = { federalEntity ->
                    selectedStatisticType = federalEntity
                    showFormModal = true
                }
            )

            if (showFormModal) {
                screenModel.resetForm()
                StatisticTypeFormModal(
                    screenModel = screenModel,
                    statisticType = selectedStatisticType,
                    onDismissRequest = { resetView() },
                    onSuccess = { resetView() }
                )
            }
        }
    }
}
