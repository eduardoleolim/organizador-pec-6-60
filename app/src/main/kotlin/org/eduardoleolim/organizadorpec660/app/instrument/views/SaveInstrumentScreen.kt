package org.eduardoleolim.organizadorpec660.app.instrument.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class SaveInstrumentScreen(
    private val instrumentId: String?,
    private val queryBus: QueryBus,
    private val commandBus: CommandBus
) : Screen {
    @Composable
    override fun Content() {
        Column(modifier = Modifier.padding(24.dp)) {

        }
    }
}
