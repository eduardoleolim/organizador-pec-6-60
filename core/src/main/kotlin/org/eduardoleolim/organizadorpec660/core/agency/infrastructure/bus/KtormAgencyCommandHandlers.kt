package org.eduardoleolim.organizadorpec660.core.agency.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.agency.application.create.AgencyCreator
import org.eduardoleolim.organizadorpec660.core.agency.application.create.CreateAgencyCommand
import org.eduardoleolim.organizadorpec660.core.agency.application.create.CreateAgencyCommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormAgencyCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command>, CommandHandler<out Command>> = mapOf(
        CreateAgencyCommand::class to createCommandHandler(),
    )

    private fun createCommandHandler(): CommandHandler<out Command> {
        val creator: AgencyCreator by inject()
        val commandHandler = CreateAgencyCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
