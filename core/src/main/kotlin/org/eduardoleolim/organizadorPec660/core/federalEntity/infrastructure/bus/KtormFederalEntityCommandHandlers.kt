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
import org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormFederalEntityCommandHandlers(private val database: Database, private val sqliteExtensions: List<String>) :
    HashMap<KClass<out Command>, CommandHandler<out Command>>() {
    private val federalEntityRepository = KtormFederalEntityRepository(database)

    init {
        this[CreateFederalEntityCommand::class] = createCommandHandler()
        this[UpdateFederalEntityCommand::class] = updateCommandHandler()
        this[DeleteFederalEntityCommand::class] = deleteCommandHandler()
    }

    private fun createCommandHandler(): CommandHandler<out Command> {
        val creator = FederalEntityCreator(federalEntityRepository)
        val commandHandler = CreateFederalEntityCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler, sqliteExtensions)
    }

    private fun updateCommandHandler(): CommandHandler<out Command> {
        val updater = FederalEntityUpdater(federalEntityRepository)
        val commandHandler = UpdateFederalEntityCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler, sqliteExtensions)
    }

    private fun deleteCommandHandler(): CommandHandler<out Command> {
        val deleter = FederalEntityDeleter(federalEntityRepository)
        val commandHandler = DeleteFederalEntityCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler, sqliteExtensions)
    }
}
