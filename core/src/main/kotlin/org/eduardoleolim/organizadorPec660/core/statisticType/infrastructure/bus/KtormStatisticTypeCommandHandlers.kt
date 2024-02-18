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

class KtormStatisticTypeCommandHandlers(database: Database) :
    HashMap<KClass<out Command>, CommandHandler<out Command>>() {
    private val statisticTypeRepository: KtormStatisticTypeRepository
    private val instrumentTypeRepository: KtormInstrumentTypeRepository

    init {
        statisticTypeRepository = KtormStatisticTypeRepository(database)
        instrumentTypeRepository = KtormInstrumentTypeRepository(database)

        this[CreateStatisticTypeCommand::class] = createCommandHandler(database)
        this[UpdateStatisticTypeCommand::class] = updateCommandHandler(database)
        this[DeleteStatisticTypeCommand::class] = deleteCommandHandler(database)
    }

    private fun createCommandHandler(database: Database): CommandHandler<out Command> {
        val creator = StatisticTypeCreator(statisticTypeRepository, instrumentTypeRepository)
        val commandHandler = CreateStatisticTypeCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(database: Database): CommandHandler<out Command> {
        val updater = StatisticTypeUpdater(statisticTypeRepository, instrumentTypeRepository)
        val commandHandler = UpdateStatisticTypeCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(database: Database): CommandHandler<out Command> {
        val deleter = StatisticTypeDeleter(statisticTypeRepository)
        val commandHandler = DeleteStatisticTypeCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
