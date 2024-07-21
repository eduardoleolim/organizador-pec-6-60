package org.eduardoleolim.organizadorpec660.app.instrument.model

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.app.router.HomeProvider
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class InstrumentScreenModel(
    private val navigator: Navigator,
    private val queryBus: QueryBus,
    private val commandBus: CommandBus
) : ScreenModel {
    fun navigateToSaveInstrumentView() {
        navigator.push(ScreenRegistry.get(HomeProvider.SaveInstrumentScreen))
    }
}
