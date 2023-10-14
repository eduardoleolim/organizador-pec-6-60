package org.eduardoleolim.core.federalEntity.infrastructure.bus

import org.eduardoleolim.core.federalEntity.application.create.CreateFederalEntityCommand
import org.eduardoleolim.core.federalEntity.application.create.CreateFederalEntityCommandHandler
import org.eduardoleolim.core.federalEntity.application.create.FederalEntityCreator
import org.eduardoleolim.core.federalEntity.application.delete.DeleteFederalEntityCommand
import org.eduardoleolim.core.federalEntity.application.delete.DeleteFederalEntityCommandHandler
import org.eduardoleolim.core.federalEntity.application.delete.FederalEntityDeleter
import org.eduardoleolim.core.federalEntity.application.update.FederalEntityUpdater
import org.eduardoleolim.core.federalEntity.application.update.UpdateFederalEntityCommand
import org.eduardoleolim.core.federalEntity.application.update.UpdateFederalEntityCommandHandler
import org.eduardoleolim.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormFederalEntityCommandHandlers(database: Database) :
    HashMap<KClass<out Command>, CommandHandler<out Command>>() {
    private val federalEntityRepository: KtormFederalEntityRepository

    init {
        federalEntityRepository = KtormFederalEntityRepository(database)

        this[CreateFederalEntityCommand::class] = createCommandHandler(database)
        this[UpdateFederalEntityCommand::class] = updateCommandHandler(database)
        this[DeleteFederalEntityCommand::class] = deleteCommandHandler(database)
    }

    private fun createCommandHandler(database: Database): CommandHandler<out Command> {
        val creator = FederalEntityCreator(federalEntityRepository)
        val commandHandler = CreateFederalEntityCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(database: Database): CommandHandler<out Command> {
        val updater = FederalEntityUpdater(federalEntityRepository)
        val commandHandler = UpdateFederalEntityCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(database: Database): CommandHandler<out Command> {
        val deleter = FederalEntityDeleter(federalEntityRepository)
        val commandHandler = DeleteFederalEntityCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
