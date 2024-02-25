package org.eduardoleolim.organizadorPec660.app.federalEntity

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
import org.eduardoleolim.organizadorPec660.app.home.HomeActions
import org.eduardoleolim.organizadorPec660.app.home.HomeTitle
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

class FederalEntityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { FederalEntityScreenModel(queryBus, commandBus) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormModal by remember { mutableStateOf(false) }
        var selectedFederalEntity by remember { mutableStateOf<FederalEntityResponse?>(null) }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var searchValue by remember { mutableStateOf("") }

        HomeTitle("Entidades federativas")
        HomeActions {
            SmallFloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = { showFormModal = true },
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
            selectedFederalEntity = null
        }

        FederalEntitiesTable(
            value = searchValue,
            onValueChange = { searchValue = it },
            pageSizes = pageSizes,
            state = state,
            data = screenModel.federalEntities.value,
            onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                val orders = orderBy?.let {
                    val orderType = if (isAscending) "ASC" else "DESC"
                    arrayOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                }

                screenModel.searchFederalEntities(search, orders, pageSize, pageIndex * pageSize)
            },
            onDeleteRequest = { federalEntity ->
                selectedFederalEntity = federalEntity
                showDeleteModal = true
            },
            onEditRequest = { federalEntity ->
                selectedFederalEntity = federalEntity
                showFormModal = true
            }
        )

        if (showDeleteModal && selectedFederalEntity != null) {
            screenModel.resetDeleteModal()
            FederalEntityDeleteModal(
                screenModel = screenModel,
                selectedFederalEntity = selectedFederalEntity!!,
                onSuccess = {
                    showDeleteModal = false
                    selectedFederalEntity = null
                    resetTable()
                },
                onFail = {
                    showDeleteModal = false
                    selectedFederalEntity = null
                },
                onDismissRequest = {
                    showDeleteModal = false
                    selectedFederalEntity = null
                }
            )
        }

        if (showFormModal) {
            screenModel.resetForm()
            FederalEntityFormModal(
                screenModel = screenModel,
                selectedFederalEntity = selectedFederalEntity,
                onDismissRequest = {
                    showFormModal = false
                    selectedFederalEntity = null
                    resetTable()
                },
                onSuccess = {
                    showFormModal = false
                    selectedFederalEntity = null
                    resetTable()
                }
            )
        }
    }
}
