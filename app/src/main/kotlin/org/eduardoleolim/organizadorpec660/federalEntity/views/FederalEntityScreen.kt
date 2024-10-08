/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.organizadorpec660.federalEntity.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource

class FederalEntityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { FederalEntityScreenModel(queryBus, commandBus, Dispatchers.IO) }
        val federalEntities = screenModel.federalEntities
        val searchParameters by screenModel.searchParameters.collectAsState()
        val search = searchParameters.search
        val screenState = screenModel.screenState
        val selectedFederalEntity = screenState.selectedFederalEntity
        val showFormModal = screenState.showFormModal
        val showDeleteModal = screenState.showDeleteModal
        val showImportExportSelector = screenState.showImportExportSelector
        val showImportModal = screenState.showImportModal
        val showExportModal = screenState.showExportModal
        val pageSizes = screenState.pageSizes
        val tableState = screenState.tableState

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            FederalEntityScreenHeader(
                onSaveRequest = { screenModel.showFormModal(null) },
                onImportExportRequest = { screenModel.showImportExportSelector() }
            )

            FederalEntitiesTable(
                modifier = Modifier.fillMaxSize(),
                data = federalEntities,
                value = search,
                onValueChange = { screenModel.searchFederalEntities(it) },
                pageSizes = pageSizes,
                state = tableState,
                onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                    val offset = pageIndex * pageSize
                    val orders = orderBy?.takeIf { it.isNotEmpty() }?.let {
                        listOf(hashMapOf("orderBy" to it, "orderType" to if (isAscending) "ASC" else "DESC"))
                    }.orEmpty()

                    screenModel.searchFederalEntities(search, orders, pageSize, offset)
                },
                onDeleteRequest = { federalEntity ->
                    screenModel.showDeleteModal(federalEntity)
                },
                onEditRequest = { federalEntity ->
                    screenModel.showFormModal(federalEntity)
                }
            )

            when {
                showFormModal -> {
                    FederalEntityFormModal(
                        screenModel = screenModel,
                        federalEntityId = selectedFederalEntity?.id,
                        onDismissRequest = { screenModel.resetScreen() },
                        onSuccess = { screenModel.resetScreen() }
                    )
                }

                showDeleteModal && selectedFederalEntity != null -> {
                    FederalEntityDeleteModal(
                        screenModel = screenModel,
                        federalEntity = selectedFederalEntity,
                        onSuccess = { screenModel.resetScreen() },
                        onDismissRequest = { screenModel.resetScreen() }
                    )
                }

                showImportExportSelector -> {
                    FederalEntityImportExportSelector(
                        onImportRequest = { screenModel.showImportModal() },
                        onExportRequest = { screenModel.showExportModal() },
                        onDismissRequest = { screenModel.resetScreen() }
                    )
                }

                showImportModal -> {
                    FederalEntityImportModal(
                        screenModel = screenModel,
                        onSuccessImport = { screenModel.resetScreen() },
                        onDismissRequest = { screenModel.resetScreen() }
                    )
                }

                showExportModal -> {}
            }
        }
    }

    @Composable
    private fun FederalEntityScreenHeader(
        onSaveRequest: () -> Unit,
        onImportExportRequest: () -> Unit
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.federal_entities),
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
                    contentDescription = "Import/Export federal entities"
                )
            }

            SmallFloatingActionButton(
                onClick = onSaveRequest,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add federal entity"
                )
            }
        }
    }

    @Composable
    fun FederalEntityImportExportSelector(
        onExportRequest: () -> Unit,
        onImportRequest: () -> Unit,
        onDismissRequest: () -> Unit
    ) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = onDismissRequest,
            title = {
                Text(stringResource(Res.string.fe_catalog_title))
            },
            text = {
                Text(stringResource(Res.string.fe_catalog_content))
            },
            confirmButton = {
                TextButton(
                    onClick = onExportRequest
                ) {
                    Text(stringResource(Res.string.fe_catalog_export))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onImportRequest
                ) {
                    Text(stringResource(Res.string.fe_catalog_import))
                }
            }
        )
    }
}
