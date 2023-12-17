package org.eduardoleolim.organizadorpec660.app.federalEntity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.IconButtonDefaults.iconButtonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import org.eduardoleolim.organizadorpec660.app.shared.components.dialogs.SearchingErrorDialog
import org.eduardoleolim.organizadorpec660.app.shared.components.table.SearchValues
import org.eduardoleolim.organizadorpec660.app.shared.components.table.Table
import org.eduardoleolim.organizadorpec660.app.shared.components.table.TableColumn
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class FederalEntityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    private val tableColumns = listOf<TableColumn<FederalEntityResponse>>(
        TableColumn(name = "Clave", width = 100.dp, formatter = { Text(it.keyCode) }),
        TableColumn(name = "Nombre", width = 300.dp, formatter = { Text(it.name) }),
        TableColumn(
            name = "Acciones",
            width = 100.dp,
            formatter = {
                val editContainerColor = MaterialTheme.colorScheme.secondaryContainer
                val editContentColor = MaterialTheme.colorScheme.secondary
                val deleteContainerColor = MaterialTheme.colorScheme.errorContainer
                val deleteContentColor = MaterialTheme.colorScheme.error

                Row {
                    FilledIconButton(
                        onClick = {
                            println("Edit")
                        },
                        modifier = Modifier.size(30.dp).padding(2.dp),
                        colors = iconButtonColors(
                            containerColor = editContainerColor,
                            contentColor = editContentColor,
                            disabledContainerColor = editContainerColor.copy(alpha = 0.5f),
                            disabledContentColor = editContentColor.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    FilledIconButton(
                        onClick = {
                            println("Delete")
                        },
                        modifier = Modifier.size(30.dp).padding(2.dp),
                        colors = iconButtonColors(
                            containerColor = deleteContainerColor,
                            contentColor = deleteContentColor,
                            disabledContainerColor = deleteContainerColor.copy(alpha = 0.5f),
                            disabledContentColor = deleteContentColor.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        )
    )

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { FederalEntityScreenModel(queryBus, commandBus) }

        Box {
            Column {
                Text(text = "Entidades federativas")

                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    FederalEntitiesTable(screenModel)
                }
            }
        }
    }

    @Composable
    private fun FederalEntitiesTable(screenModel: FederalEntityScreenModel) {
        var searchingError by remember { mutableStateOf(false) }

        fun onFederalEntitiesSearchRequest(
            searchValues: SearchValues,
            load: (List<FederalEntityResponse>, Int) -> Unit
        ) {
            val (search, pageNumber, pageSize, orderType, orderBy) = searchValues
            val offset = if (pageNumber <= 1) 0 else (pageNumber - 1) * pageSize
            val orders = if (orderBy != null && orderType != null) arrayOf(
                hashMapOf(
                    "orderBy" to orderBy,
                    "orderType" to orderType
                )
            ) else null

            screenModel.searchFederalEntities(search, orders, pageSize, offset)
                .fold(
                    onSuccess = {
                        load(it.federalEntities, it.totalRecords)
                    },
                    onFailure = {
                        searchingError = true
                        load(emptyList(), 0)
                    }
                )
        }

        Table(
            columns = tableColumns,
            onSearchRequest = ::onFederalEntitiesSearchRequest
        )

        if (searchingError) {
            SearchingErrorDialog(
                onDismissRequest = { searchingError = false },
                confirmButton = {
                    Button(
                        onClick = { searchingError = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Aceptar")
                    }
                },
                title = {
                    Text("Error")
                },
                text = {
                    Text("Error al cargar las entidades federativas")
                }
            )
        }
    }
}
