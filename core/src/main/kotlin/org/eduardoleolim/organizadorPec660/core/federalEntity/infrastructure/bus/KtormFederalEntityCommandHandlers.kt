package org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.federalEntity.application.create.CreateFederalEntityCommand
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.create.CreateFederalEntityCommandHandler
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.create.FederalEntityCreator
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.delete.DeleteFederalEntityCommand
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.delete.DeleteFederalEntityCommandHandler
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.delete.FederalEntityDeleter
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.update.FederalEntityUpdater
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.update.UpdateFederalEntityCommand
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.update.UpdateFederalEntityCommandHandler
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormFederalEntityCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command>, CommandHandler<out Command>> = mapOf(
        CreateFederalEntityCommand::class to createCommandHandler(),
        UpdateFederalEntityCommand::class to updateCommandHandler(),
        DeleteFederalEntityCommand::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<out Command> {
        val creator: FederalEntityCreator by inject()
        val commandHandler = CreateFederalEntityCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<out Command> {
        val updater: FederalEntityUpdater by inject()
        val commandHandler = UpdateFederalEntityCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<out Command> {
        val deleter: FederalEntityDeleter by inject()
        val commandHandler = DeleteFederalEntityCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
