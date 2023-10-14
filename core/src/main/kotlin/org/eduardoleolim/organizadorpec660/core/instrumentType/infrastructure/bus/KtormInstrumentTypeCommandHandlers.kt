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
import org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.persistence.KtormInstrumentTypeRepository
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormInstrumentTypeCommandHandlers(database: Database) :
    HashMap<KClass<out Command>, CommandHandler<out Command>>() {
    private val instrumentTypeRepository: KtormInstrumentTypeRepository

    init {
        instrumentTypeRepository = KtormInstrumentTypeRepository(database)

        this[CreateInstrumentTypeCommand::class] = createCommandHandler(database)
        this[UpdateInstrumentTypeCommand::class] = updateCommandHandler(database)
        this[DeleteInstrumentTypeCommand::class] = deleteCommandHandler(database)
    }

    private fun createCommandHandler(database: Database): CommandHandler<out Command> {
        val creator = InstrumentTypeCreator(instrumentTypeRepository)
        val commandHandler = CreateInstrumentTypeCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(database: Database): CommandHandler<out Command> {
        val updater = InstrumentTypeUpdater(instrumentTypeRepository)
        val commandHandler = UpdateInstrumentTypeCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(database: Database): CommandHandler<out Command> {
        val deleter = InstrumentTypeDeleter(instrumentTypeRepository)
        val commandHandler = DeleteInstrumentTypeCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
