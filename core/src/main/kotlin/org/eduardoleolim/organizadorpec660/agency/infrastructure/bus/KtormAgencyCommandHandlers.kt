package org.eduardoleolim.organizadorpec660.agency.infrastructure.bus

import org.eduardoleolim.organizadorpec660.agency.application.create.AgencyCreator
import org.eduardoleolim.organizadorpec660.agency.application.create.CreateAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.create.CreateAgencyCommandHandler
import org.eduardoleolim.organizadorpec660.agency.application.delete.AgencyDeleter
import org.eduardoleolim.organizadorpec660.agency.application.delete.DeleteAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.delete.DeleteAgencyCommandHandler
import org.eduardoleolim.organizadorpec660.agency.application.update.AgencyUpdater
import org.eduardoleolim.organizadorpec660.agency.application.update.UpdateAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.update.UpdateAgencyCommandHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormAgencyCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>> = mapOf(
        CreateAgencyCommand::class to createCommandHandler(),
        UpdateAgencyCommand::class to updateCommandHandler(),
        DeleteAgencyCommand::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val creator: AgencyCreator by inject()
        val commandHandler = CreateAgencyCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: AgencyUpdater by inject()
        val commandHandler = UpdateAgencyCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val deleter: AgencyDeleter by inject()
        val commandHandler = DeleteAgencyCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
