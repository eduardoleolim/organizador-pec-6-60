package org.eduardoleolim.organizadorpec660.app.agency.model

import cafe.adriel.voyager.core.model.ScreenModel
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class AgencyScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
}
