package org.eduardoleolim.organizadorpec660.app.federalEntity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

class FederalEntityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { FederalEntityScreenModel(queryBus, commandBus) }
        var search: String by remember { mutableStateOf("") }
        var limit: Int? by remember { mutableStateOf(10) }
        var offset: Int? by remember { mutableStateOf(0) }

        val federalEntities = screenModel.searchFederalEntities(
            search = search,
            limit = limit,
            offset = offset
        )

        Box {
            Column {
                Text("Entidades federativas")

                LazyColumn {
                    item {
                        Row {
                            Text("Clave")
                            Text("Nombre")
                        }
                    }

                    items(federalEntities) { federalEntity ->
                        Row {
                            Text(federalEntity.keyCode)
                            Text(federalEntity.name)
                        }
                    }
                }
            }
        }
    }
}
