package org.eduardoleolim.organizadorPec660.core.instrumentType.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.instrumentType.application.create.CreateInstrumentTypeCommand
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.create.CreateInstrumentTypeCommandHandler
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.create.InstrumentTypeCreator
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.delete.DeleteInstrumentTypeCommand
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.delete.DeleteInstrumentTypeCommandHandler
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.delete.InstrumentTypeDeleter
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.update.InstrumentTypeUpdater
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.update.UpdateInstrumentTypeCommand
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.update.UpdateInstrumentTypeCommandHandler
import org.eduardoleolim.organizadorPec660.core.instrumentType.infrastructure.persistence.KtormInstrumentTypeRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormInstrumentTypeCommandHandlers(private val database: Database, private val sqliteExtensions: List<String>) :
    HashMap<KClass<out Command>, CommandHandler<out Command>>() {
    private val instrumentTypeRepository = KtormInstrumentTypeRepository(database)

    init {
        this[CreateInstrumentTypeCommand::class] = createCommandHandler()
        this[UpdateInstrumentTypeCommand::class] = updateCommandHandler()
        this[DeleteInstrumentTypeCommand::class] = deleteCommandHandler()
    }

    private fun createCommandHandler(): CommandHandler<out Command> {
        val creator = InstrumentTypeCreator(instrumentTypeRepository)
        val commandHandler = CreateInstrumentTypeCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler, sqliteExtensions)
    }

    private fun updateCommandHandler(): CommandHandler<out Command> {
        val updater = InstrumentTypeUpdater(instrumentTypeRepository)
        val commandHandler = UpdateInstrumentTypeCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler, sqliteExtensions)
    }

    private fun deleteCommandHandler(): CommandHandler<out Command> {
        val deleter = InstrumentTypeDeleter(instrumentTypeRepository)
        val commandHandler = DeleteInstrumentTypeCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler, sqliteExtensions)
    }
}
