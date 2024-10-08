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

package org.eduardoleolim.organizadorpec660.municipality.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.municipality.model.MunicipalityScreenModel
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.municipalities
import org.jetbrains.compose.resources.stringResource

class MunicipalityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { MunicipalityScreenModel(queryBus, commandBus, Dispatchers.IO) }
        val municipalities = screenModel.municipalities
        val federalEntities = screenModel.federalEntities
        val searchParameters by screenModel.searchParameters.collectAsState()
        val search = searchParameters.search
        val federalEntityFilter = searchParameters.federalEntity
        val screenState = screenModel.screenState
        val selectedMunicipality = screenState.selectedMunicipality
        val showFormModal = screenState.showFormModal
        val showDeleteModal = screenState.showDeleteModal
        val pageSizes = screenState.pageSizes
        val tableState = screenState.tableState

        LaunchedEffect(Unit) {
            screenModel.searchAllFederalEntities()
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            MunicipalityScreenHeader(
                onSaveRequest = { screenModel.showFormModal(null) },
                onImportExportRequest = { }
            )

            MunicipalitiesTable(
                modifier = Modifier.fillMaxSize(),
                data = municipalities,
                federalEntities = federalEntities,
                federalEntity = federalEntityFilter,
                onFederalEntitySelected = { screenModel.searchMunicipalities(federalEntity = it) },
                value = search,
                onValueChange = { screenModel.searchMunicipalities(it) },
                pageSizes = pageSizes,
                state = tableState,
                onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                    val offset = pageIndex * pageSize
                    val orders = orderBy?.takeIf { it.isNotEmpty() }?.let {
                        listOf(hashMapOf("orderBy" to it, "orderType" to if (isAscending) "ASC" else "DESC"))
                    }.orEmpty()

                    screenModel.searchMunicipalities(
                        search = search,
                        orders = orders,
                        limit = pageSize,
                        offset = offset
                    )
                },
                onDeleteRequest = { screenModel.showDeleteModal(it) },
                onEditRequest = { screenModel.showFormModal(it) }
            )

            when {
                showFormModal -> {
                    MunicipalityFormModal(
                        screenModel = screenModel,
                        municipalityId = selectedMunicipality?.id,
                        onDismissRequest = { screenModel.resetScreen() },
                        onSuccess = { screenModel.resetScreen() }
                    )
                }

                showDeleteModal && selectedMunicipality != null -> {
                    MunicipalityDeleteModal(
                        screenModel = screenModel,
                        municipality = selectedMunicipality,
                        onSuccess = { screenModel.resetScreen() },
                        onDismissRequest = { screenModel.resetScreen() }
                    )
                }
            }
        }
    }

    @Composable
    private fun MunicipalityScreenHeader(
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
