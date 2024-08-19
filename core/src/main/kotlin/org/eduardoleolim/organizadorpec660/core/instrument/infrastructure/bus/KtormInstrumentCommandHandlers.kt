package org.eduardoleolim.organizadorpec660.core.instrument.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.instrument.application.create.CreateInstrumentCommand
import org.eduardoleolim.organizadorpec660.core.instrument.application.create.CreateInstrumentCommandHandler
import org.eduardoleolim.organizadorpec660.core.instrument.application.create.InstrumentCreator
import org.eduardoleolim.organizadorpec660.core.instrument.application.delete.DeleteInstrumentCommand
import org.eduardoleolim.organizadorpec660.core.instrument.application.delete.DeleteInstrumentCommandHandler
import org.eduardoleolim.organizadorpec660.core.instrument.application.delete.InstrumentDeleter
import org.eduardoleolim.organizadorpec660.core.instrument.application.save.*
import org.eduardoleolim.organizadorpec660.core.instrument.application.update.InstrumentUpdater
import org.eduardoleolim.organizadorpec660.core.instrument.application.update.UpdateInstrumentCommand
import org.eduardoleolim.organizadorpec660.core.instrument.application.update.UpdateInstrumentCommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormInstrumentCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>> = mapOf(
        CreateInstrumentCommand::class to createCommandHandler(),
        UpdateInstrumentCommand::class to updateCommandHandler(),
        UpdateInstrumentAsSavedCommand::class to updateAsSavedCommandHandler(),
        UpdateInstrumentAsNotSavedCommand::class to updateAsNotSavedCommandHandler(),
        DeleteInstrumentCommand::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val creator: InstrumentCreator by inject()
        val commandHandler = CreateInstrumentCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: InstrumentUpdater by inject()
        val commandHandler = UpdateInstrumentCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateAsSavedCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: InstrumentSiresoSaver by inject()
        val commandHandler = UpdateInstrumentAsSavedCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateAsNotSavedCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: InstrumentSiresoSaver by inject()
        val commandHandler = UpdateInstrumentAsNotSavedCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val deleter: InstrumentDeleter by inject()
        val commandHandler = DeleteInstrumentCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
