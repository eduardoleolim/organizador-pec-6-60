package org.eduardoleolim.organizadorpec660.instrument.views

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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.instrument.model.InstrumentScreenModel
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.notification.LocalTrayState
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.instruments
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
        val pageSizes = screenState.pageSizes
        val tableState = screenState.tableState

        LaunchedEffect(Unit) {
            screenModel.initializeScreen()
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
                onImportExportRequest = { }
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
}
