package org.eduardoleolim.organizadorPec660.app.instrumentType

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import org.eduardoleolim.organizadorPec660.app.home.HomeActions
import org.eduardoleolim.organizadorPec660.app.home.HomeTitle
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

class InstrumentTypeScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
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
    }
}
