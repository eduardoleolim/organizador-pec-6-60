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

package org.eduardoleolim.organizadorpec660.instrument.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.instrument.model.InstrumentScreenModel
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.notification.LocalTrayState
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource

class InstrumentScreen(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val tempDirectory: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val trayState = LocalTrayState.current
        val screenModel = rememberScreenModel {
            InstrumentScreenModel(navigator, trayState, queryBus, commandBus, tempDirectory, Dispatchers.IO)
        }
        val instruments = screenModel.instruments
        val statisticTypes = screenModel.statisticTypes
        val federalEntities = screenModel.federalEntities
        val municipalities = screenModel.municipalities
        val agencies = screenModel.agencies
        val searchParameters by screenModel.searchParameters.collectAsState()
        val search = searchParameters.search
        val statisticYearFilter = searchParameters.statisticYear
        val statisticMonthFilter = searchParameters.statisticMonth
        val statisticTypeFilter = searchParameters.statisticType
        val federalEntityFilter = searchParameters.federalEntity
        val municipalityFilter = searchParameters.municipality
        val agencyFilter = searchParameters.agency
        val screenState = screenModel.screenState
        val statisticYears = screenState.statisticYears
        val statisticMonths = screenState.statisticMonths
        val showImportExportSelector = screenState.showImportExportSelector
        val showImportModal = screenState.showImportModal
        val showExportModal = screenState.showExportModal
        val pageSizes = screenState.pageSizes
        val tableState = screenState.tableState

        LaunchedEffect(Unit) {
            screenModel.setInitialMode()
        }

        LaunchedEffect(federalEntityFilter) {
            screenModel.searchMunicipalities(federalEntityFilter?.id)
        }

        LaunchedEffect(municipalityFilter) {
            screenModel.searchAgencies(municipalityFilter?.id)
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            InstrumentScreenHeader(
                onSaveRequest = { screenModel.navigateToSaveInstrumentView(null) },
                onImportExportRequest = { screenModel.showImportExportSelector() }
            )

            InstrumentsTable(
                modifier = Modifier.fillMaxSize(),
                data = instruments,
                value = search,
                onValueChange = { screenModel.searchInstruments(search = it) },
                statisticYears = statisticYears,
                statisticYear = statisticYearFilter,
                onStatisticYearSelected = { screenModel.searchInstruments(statisticYear = it) },
                statisticMonths = statisticMonths,
                statisticMonth = statisticMonthFilter,
                onStatisticMonthSelected = { screenModel.searchInstruments(statisticMonth = it) },
                statisticTypes = statisticTypes,
                statisticType = statisticTypeFilter,
                onStatisticTypeSelected = { screenModel.searchInstruments(statisticType = it) },
                federalEntities = federalEntities,
                federalEntity = federalEntityFilter,
                onFederalEntitySelected = { federalEntity ->
                    val isDifferentFederalEntity = federalEntity?.id != federalEntityFilter?.id
                    screenModel.searchInstruments(
                        federalEntity = federalEntity,
                        municipality = if (isDifferentFederalEntity) null else municipalityFilter,
                        agency = if (isDifferentFederalEntity) null else agencyFilter
                    )
                },
                municipalities = municipalities,
                municipality = municipalityFilter,
                onMunicipalitySelected = { municipality ->
                    val isDifferentMunicipality = municipality?.id != municipalityFilter?.id
                    screenModel.searchInstruments(
                        municipality = municipality,
                        agency = if (isDifferentMunicipality) null else agencyFilter
                    )
                },
                agencies = agencies,
                agency = agencyFilter,
                onAgencySelected = { screenModel.searchInstruments(agency = it) },
                pageSizes = pageSizes,
                state = tableState,
                onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                    val offset = pageIndex * pageSize
                    val orders = orderBy?.takeIf { it.isNotEmpty() }?.let {
                        listOf(hashMapOf("orderBy" to it, "orderType" to if (isAscending) "ASC" else "DESC"))
                    }.orEmpty()

                    screenModel.searchInstruments(search = search, orders = orders, limit = pageSize, offset = offset)
                },
                onDeleteRequest = { screenModel.deleteInstrument(it.id) },
                onEditRequest = { screenModel.navigateToSaveInstrumentView(it.id) },
                onCopyRequest = { screenModel.copyInstrumentToClipboard(it.id) },
                onShowDetailsRequest = { screenModel.navigateToShowInstrumentDetailsView(it.id) },
                onChangeSiresoStatusRequest = { instrument, save ->
                    if (save) {
                        screenModel.updateInstrumentAsSavedInSIRESO(instrument.id)
                    } else {
                        screenModel.updateInstrumentAsNotSavedInSIRESO(instrument.id)
                    }
                }
            )
        }

        when {
            showImportExportSelector -> {
                InstrumentImportExportSelector(
                    onExportRequest = { screenModel.showExportModal() },
                    onImportRequest = { screenModel.showImportModal() },
                    onDismissRequest = { screenModel.setInitialMode() }
                )
            }

            showImportModal -> {

            }

            showExportModal -> {

            }
        }
    }

    @Composable
    private fun InstrumentScreenHeader(
        onSaveRequest: () -> Unit,
        onImportExportRequest: () -> Unit
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.instruments),
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
                    contentDescription = "Import/Export instruments"
                )
            }

            SmallFloatingActionButton(
                onClick = onSaveRequest,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add instrument"
                )
            }
        }
    }

    @Composable
    private fun InstrumentImportExportSelector(
        onExportRequest: () -> Unit,
        onImportRequest: () -> Unit,
        onDismissRequest: () -> Unit
    ) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = onDismissRequest,
            title = {
                Text(stringResource(Res.string.inst_catalog_title))
            },
            text = {
                Text(stringResource(Res.string.inst_catalog_content))
            },
            confirmButton = {
                TextButton(
                    onClick = onExportRequest
                ) {
                    Text(stringResource(Res.string.inst_catalog_export))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onImportRequest
                ) {
                    Text(stringResource(Res.string.inst_catalog_import))
                }
            }
        )
    }
}
