/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.statisticType.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.statistic_types
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeScreenModel
import org.jetbrains.compose.resources.stringResource

class StatisticTypeScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { StatisticTypeScreenModel(queryBus, commandBus, Dispatchers.IO) }
        val statisticTypes = screenModel.statisticTypes
        val searchParameters by screenModel.searchParameters.collectAsState()
        val search = searchParameters.search
        val screenState = screenModel.screenState
        val selectedStatisticType = screenState.selectedStatisticType
        val showFormModal = screenState.showFormModal
        val showDeleteModal = screenState.showDeleteModal
        val pageSizes = screenState.pageSizes
        val tableState = screenState.tableState

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            StatisticTypeScreenHeader(
                onSaveRequest = { screenModel.showFormModal(null) },
                onImportExportRequest = { }
            )

            StatisticTypeTable(
                modifier = Modifier.fillMaxSize(),
                data = statisticTypes,
                value = search,
                onValueChange = { screenModel.searchStatisticTypes(it) },
                pageSizes = pageSizes,
                state = tableState,
                onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                    val offset = pageIndex * pageSize
                    val orders = orderBy?.takeIf { it.isNotEmpty() }?.let {
                        listOf(hashMapOf("orderBy" to it, "orderType" to if (isAscending) "ASC" else "DESC"))
                    }.orEmpty()

                    screenModel.searchStatisticTypes(search, orders, pageSize, offset)
                },
                onDeleteRequest = { statisticType ->
                    screenModel.showDeleteModal(statisticType)
                },
                onEditRequest = { statisticType ->
                    screenModel.showFormModal(statisticType)
                }
            )

            when {
                showFormModal -> {
                    StatisticTypeFormModal(
                        screenModel = screenModel,
                        statisticTypeId = selectedStatisticType?.id,
                        onDismissRequest = { screenModel.resetScreen() },
                        onSuccess = { screenModel.resetScreen() }
                    )
                }

                showDeleteModal && selectedStatisticType != null -> {
                    StatisticTypeDeleteModal(
                        screenModel = screenModel,
                        statisticType = selectedStatisticType,
                        onSuccess = { screenModel.resetScreen() },
                        onDismissRequest = { screenModel.resetScreen() }
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
