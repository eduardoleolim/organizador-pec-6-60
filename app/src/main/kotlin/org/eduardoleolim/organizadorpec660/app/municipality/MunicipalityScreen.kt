package org.eduardoleolim.organizadorpec660.app.municipality

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import org.eduardoleolim.organizadorpec660.app.home.HomeActions
import org.eduardoleolim.organizadorpec660.app.home.HomeTitle
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class MunicipalityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        HomeTitle("Municipios")
        HomeActions {
            SmallFloatingActionButton(
                onClick = { println("Crear municipio") },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Filled.Add, "Agregar entidad federativa")
            }
        }
    }
}
