package org.eduardoleolim.organizadorpec660.app.instrument

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import org.eduardoleolim.organizadorpec660.app.home.HomeTitle
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class InstrumentScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        HomeTitle("Instrumentos")
    }
}
