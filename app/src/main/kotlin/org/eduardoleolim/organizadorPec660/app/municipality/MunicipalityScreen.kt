package org.eduardoleolim.organizadorPec660.app.municipality

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
import org.eduardoleolim.organizadorPec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

class MunicipalityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { MunicipalityScreenModel(queryBus, commandBus) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormModal by remember { mutableStateOf(false) }
        var selectedMunicipality by remember { mutableStateOf<MunicipalityResponse?>(null) }
        var searchValue by remember { mutableStateOf("") }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())

        fun resetView() {
            searchValue = ""
            state.pageIndex = -1
            state.pageSize = pageSizes.first()
            showDeleteModal = false
            showFormModal = false
            selectedMunicipality = null
            screenModel.searchAllFederalEntities()
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
                    text = "Municipios",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.weight(1.0f)
                )

                SmallFloatingActionButton(
                    onClick = {
                        selectedMunicipality = null
                        showFormModal = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar municipio"
                    )
                }
            }

            MunicipalitiesTable(
                modifier = Modifier.fillMaxSize(),
                screenModel = screenModel,
                value = searchValue,
                onValueChange = { searchValue = it },
                pageSizes = pageSizes,
                data = screenModel.municipalities.value,
                state = state,
                onSearch = { search, federalEntityId, pageIndex, pageSize, orderBy, isAscending ->
                    val orders = orderBy?.let {
                        val orderType = if (isAscending) "ASC" else "DESC"
                        arrayOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                    }

                    screenModel.searchMunicipalities(search, federalEntityId, orders, pageSize, pageIndex * pageSize)
                },
                onDeleteRequest = { federalEntity ->
                    selectedMunicipality = federalEntity
                    showDeleteModal = true
                },
                onEditRequest = { federalEntity ->
                    selectedMunicipality = federalEntity
                    showFormModal = true
                }
            )

            if (showDeleteModal && selectedMunicipality != null) {
                screenModel.resetDeleteModal()
                MunicipalityDeleteModal(
                    screenModel = screenModel,
                    municipality = selectedMunicipality!!,
                    onSuccess = { resetView() },
                    onFail = { resetView() },
                    onDismissRequest = { resetView() }
                )
            }

            if (showFormModal) {
                screenModel.resetForm()
                MunicipalityFormModal(
                    screenModel = screenModel,
                    municipality = selectedMunicipality,
                    onDismissRequest = { resetView() },
                    onSuccess = { resetView() }
                )
            }
        }
    }
}
