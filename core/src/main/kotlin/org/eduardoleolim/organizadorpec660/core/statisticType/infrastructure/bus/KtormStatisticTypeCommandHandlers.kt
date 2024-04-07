package org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.core.statisticType.application.create.CreateStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.core.statisticType.application.create.CreateStatisticTypeCommandHandler
import org.eduardoleolim.organizadorpec660.core.statisticType.application.create.StatisticTypeCreator
import org.eduardoleolim.organizadorpec660.core.statisticType.application.delete.DeleteStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.core.statisticType.application.delete.DeleteStatisticTypeCommandHandler
import org.eduardoleolim.organizadorpec660.core.statisticType.application.delete.StatisticTypeDeleter
import org.eduardoleolim.organizadorpec660.core.statisticType.application.update.StatisticTypeUpdater
import org.eduardoleolim.organizadorpec660.core.statisticType.application.update.UpdateStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.core.statisticType.application.update.UpdateStatisticTypeCommandHandler
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormStatisticTypeCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command>, CommandHandler<out Command>> = mapOf(
        CreateStatisticTypeCommand::class to createCommandHandler(),
        UpdateStatisticTypeCommand::class to updateCommandHandler(),
        DeleteStatisticTypeCommand::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<out Command> {
        val creator: StatisticTypeCreator by inject()
        val commandHandler = CreateStatisticTypeCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<out Command> {
        val updater: StatisticTypeUpdater by inject()
        val commandHandler = UpdateStatisticTypeCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<out Command> {
        val deleter: StatisticTypeDeleter by inject()
        val commandHandler = DeleteStatisticTypeCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
