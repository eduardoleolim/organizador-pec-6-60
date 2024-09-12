package org.eduardoleolim.organizadorpec660.instrument.views

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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.instruments
import org.eduardoleolim.organizadorpec660.instrument.model.InstrumentScreenModel
import org.eduardoleolim.organizadorpec660.shared.notification.LocalTrayState
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
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
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var searchValue by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            InstrumentScreenHeader(
                onSaveRequest = { screenModel.navigateToSaveInstrumentView(null) },
                onImportExportRequest = { }
            )

            InstrumentsTable(
                modifier = Modifier.fillMaxSize(),
                value = searchValue,
                screenModel = screenModel,
                onValueChange = { searchValue = it },
                pageSizes = pageSizes,
                state = state,
                data = screenModel.instruments,
                onSearch = { search, federalEntityId, municipalityId, agencyId, statisticTypeId, statisticYear, statisticMonth, pageIndex, pageSize, orderBy, isAscending ->
                    val orders = orderBy?.let {
                        val orderType = if (isAscending) "ASC" else "DESC"
                        listOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                    }

                    screenModel.searchInstruments(
                        search,
                        federalEntityId,
                        municipalityId,
                        agencyId,
                        statisticTypeId,
                        statisticYear,
                        statisticMonth,
                        orders,
                        pageSize,
                        pageIndex * pageSize
                    )
                },
                onDeleteRequest = { screenModel.deleteInstrument(it.id) },
                onEditRequest = { screenModel.navigateToSaveInstrumentView(it.id) },
                onCopyRequest = { screenModel.copyInstrumentToClipboard(it.id) },
                onShowDetailsRequest = { screenModel.navigateToShowInstrumentDetailsView(it.id) },
                onChangeStateRequest = { instrument, save ->
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
