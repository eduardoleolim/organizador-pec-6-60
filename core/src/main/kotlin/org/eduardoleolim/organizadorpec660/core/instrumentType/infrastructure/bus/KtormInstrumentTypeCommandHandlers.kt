package org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.instrumentType.application.create.CreateInstrumentTypeCommand
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.create.CreateInstrumentTypeCommandHandler
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.create.InstrumentTypeCreator
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.delete.DeleteInstrumentTypeCommand
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.delete.DeleteInstrumentTypeCommandHandler
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.delete.InstrumentTypeDeleter
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.update.InstrumentTypeUpdater
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.update.UpdateInstrumentTypeCommand
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.update.UpdateInstrumentTypeCommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormInstrumentTypeCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command>, CommandHandler<out Command>> = mapOf(
        CreateInstrumentTypeCommand::class to createCommandHandler(),
        UpdateInstrumentTypeCommand::class to updateCommandHandler(),
        DeleteInstrumentTypeCommand::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<out Command> {
        val creator: InstrumentTypeCreator by inject()
        val commandHandler = CreateInstrumentTypeCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<out Command> {
        val updater: InstrumentTypeUpdater by inject()
        val commandHandler = UpdateInstrumentTypeCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<out Command> {
        val deleter: InstrumentTypeDeleter by inject()
        val commandHandler = DeleteInstrumentTypeCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
