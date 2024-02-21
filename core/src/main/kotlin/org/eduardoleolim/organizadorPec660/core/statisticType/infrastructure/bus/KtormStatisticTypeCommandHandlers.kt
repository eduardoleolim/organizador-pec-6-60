package org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.instrumentType.infrastructure.persistence.KtormInstrumentTypeRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorPec660.core.statisticType.application.create.CreateStatisticTypeCommand
import org.eduardoleolim.organizadorPec660.core.statisticType.application.create.CreateStatisticTypeCommandHandler
import org.eduardoleolim.organizadorPec660.core.statisticType.application.create.StatisticTypeCreator
import org.eduardoleolim.organizadorPec660.core.statisticType.application.delete.DeleteStatisticTypeCommand
import org.eduardoleolim.organizadorPec660.core.statisticType.application.delete.DeleteStatisticTypeCommandHandler
import org.eduardoleolim.organizadorPec660.core.statisticType.application.delete.StatisticTypeDeleter
import org.eduardoleolim.organizadorPec660.core.statisticType.application.update.StatisticTypeUpdater
import org.eduardoleolim.organizadorPec660.core.statisticType.application.update.UpdateStatisticTypeCommand
import org.eduardoleolim.organizadorPec660.core.statisticType.application.update.UpdateStatisticTypeCommandHandler
import org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.persistence.KtormStatisticTypeRepository
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormStatisticTypeCommandHandlers(private val database: Database, private val sqliteExtensions: List<String>) :
    HashMap<KClass<out Command>, CommandHandler<out Command>>() {
    private val statisticTypeRepository = KtormStatisticTypeRepository(database)
    private val instrumentTypeRepository = KtormInstrumentTypeRepository(database)

    init {
        this[CreateStatisticTypeCommand::class] = createCommandHandler()
        this[UpdateStatisticTypeCommand::class] = updateCommandHandler()
        this[DeleteStatisticTypeCommand::class] = deleteCommandHandler()
    }

    private fun createCommandHandler(): CommandHandler<out Command> {
        val creator = StatisticTypeCreator(statisticTypeRepository, instrumentTypeRepository)
        val commandHandler = CreateStatisticTypeCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler, sqliteExtensions)
    }

    private fun updateCommandHandler(): CommandHandler<out Command> {
        val updater = StatisticTypeUpdater(statisticTypeRepository, instrumentTypeRepository)
        val commandHandler = UpdateStatisticTypeCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler, sqliteExtensions)
    }

    private fun deleteCommandHandler(): CommandHandler<out Command> {
        val deleter = StatisticTypeDeleter(statisticTypeRepository)
        val commandHandler = DeleteStatisticTypeCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler, sqliteExtensions)
    }
}
