package org.eduardoleolim.organizadorPec660.app.instrumentType

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import org.eduardoleolim.organizadorPec660.app.home.HomeActions
import org.eduardoleolim.organizadorPec660.app.home.HomeTitle
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

class InstrumentTypeScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { InstrumentTypeScreenModel(queryBus, commandBus) }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var searchValue by remember { mutableStateOf("") }

        HomeTitle("Tipos de Instrumento")
        HomeActions {
            SmallFloatingActionButton(
                onClick = { println("Crear tipo de instrumento") },
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
            // showDeleteModal = false
            // selectedInstrumentType = null
        }

        InstrumentTypeTable(
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
            onDeleteRequest = {},
            onEditRequest = {}
        )
    }
}
