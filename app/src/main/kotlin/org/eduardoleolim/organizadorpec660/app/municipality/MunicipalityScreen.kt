package org.eduardoleolim.organizadorpec660.app.municipality

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import org.eduardoleolim.organizadorpec660.app.home.HomeActions
import org.eduardoleolim.organizadorpec660.app.home.HomeTitle
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class MunicipalityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { MunicipalityScreenModel(queryBus, commandBus) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormModal by remember { mutableStateOf(false) }
        var selectedMunicipality by remember { mutableStateOf<MunicipalityResponse?>(null) }
        var municipalitiesResponse by remember { mutableStateOf(MunicipalitiesResponse(emptyList(), 0, null, null)) }
        var searchValue by remember { mutableStateOf("") }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())

        HomeTitle("Municipios")
        HomeActions {
            SmallFloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    selectedMunicipality = null
                    showFormModal = true
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Filled.Add, "Agregar entidad federativa")
            }
        }

        fun resetTable() {
            searchValue = ""
            state.pageIndex = -1
            state.pageSize = pageSizes.first()
            showDeleteModal = false
            selectedMunicipality = null
        }

        MunicipalitiesTable(
            screenModel = screenModel,
            value = searchValue,
            onValueChange = { searchValue = it },
            pageSizes = pageSizes,
            data = municipalitiesResponse,
            state = state,
            onSearch = { search, federalEntityId, pageIndex, pageSize, orderBy, isAscending ->
                val order = orderBy?.let {
                    val orderType = if (isAscending) "ASC" else "DESC"
                    arrayOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                }

                screenModel.searchMunicipalities(
                    search = search,
                    federalEntityId = federalEntityId,
                    limit = pageSize,
                    offset = pageIndex * pageSize,
                    orders = order
                ) { result ->
                    result.fold(
                        onSuccess = {
                            municipalitiesResponse = it
                        },
                        onFailure = {
                            println(it.localizedMessage)
                        }
                    )
                }
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
            MunicipalityDeleteModal(
                screenModel = screenModel,
                selectedMunicipality = selectedMunicipality!!,
                onSuccess = {
                    resetTable()
                    showDeleteModal = false
                },
                onFail = {
                    resetTable()
                    showDeleteModal = false
                },
                onDismissRequest = { showDeleteModal = false }
            )
        }

        if (showFormModal) {
            MunicipalityFormModal(
                screenModel = screenModel,
                selectedMunicipality = selectedMunicipality,
                onDismissRequest = {
                    resetTable()
                    showFormModal = false
                    selectedMunicipality = null
                },
                onSuccess = {
                    resetTable()
                    showFormModal = false
                    selectedMunicipality = null
                }
            )
        }
    }
}
