package org.eduardoleolim.organizadorpec660.app.statisticType

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class StatisticTypeScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        Box {
            Column {
                Text("Tipos de estadística")
            }
        }
    }
}
