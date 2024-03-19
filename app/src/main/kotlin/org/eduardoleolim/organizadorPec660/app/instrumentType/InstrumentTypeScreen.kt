package org.eduardoleolim.organizadorPec660.app.instrumentType

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
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypeResponse
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

class InstrumentTypeScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { InstrumentTypeScreenModel(queryBus, commandBus) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormModal by remember { mutableStateOf(false) }
        var selectedInstrumentType by remember { mutableStateOf<InstrumentTypeResponse?>(null) }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var searchValue by remember { mutableStateOf("") }

        fun resetView() {
            searchValue = ""
            state.pageIndex = -1
            state.pageSize = pageSizes.first()
            showDeleteModal = false
            showFormModal = false
            selectedInstrumentType = null
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
                    text = "Tipos de instrumento",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.weight(1.0f)
                )

                SmallFloatingActionButton(
                    onClick = { showFormModal = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar tipo de instrumento"
                    )
                }
            }


            InstrumentTypeTable(
                modifier = Modifier.fillMaxSize(),
                value = searchValue,
                onValueChange = { searchValue = it },
                pageSizes = pageSizes,
                data = screenModel.instrumentTypes.value,
                state = state,
                onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                    val orders = orderBy?.let {
                        val orderType = if (isAscending) "ASC" else "DESC"
                        arrayOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                    }

                    screenModel.searchInstrumentTypes(search, orders, pageSize, pageIndex * pageSize)
                },
                onDeleteRequest = { instrumentType ->
                    selectedInstrumentType = instrumentType
                    showDeleteModal = true
                },
                onEditRequest = { instrumentType ->
                    selectedInstrumentType = instrumentType
                    showFormModal = true
                }
            )

            if (showDeleteModal && selectedInstrumentType != null) {
                screenModel.resetDeleteModal()
                InstrumentTypeDeleteModal(
                    screenModel = screenModel,
                    instrumentType = selectedInstrumentType!!,
                    onSuccess = { resetView() },
                    onFail = { resetView() },
                    onDismissRequest = { resetView() }
                )
            }

            if (showFormModal) {
                screenModel.resetForm()
                InstrumentTypeFormModal(
                    screenModel = screenModel,
                    instrumentType = selectedInstrumentType,
                    onSuccess = { resetView() },
                    onDismissRequest = { resetView() }
                )
            }
        }
    }
}

