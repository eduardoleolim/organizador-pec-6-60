package org.eduardoleolim.core.shared.infrastructure.bus

import org.eduardoleolim.core.federalEntity.infrastructure.bus.KtormFederalEntityCommandHandlers
import org.eduardoleolim.core.instrumentType.infrastructure.bus.KtormInstrumentTypeCommandHandlers
import org.eduardoleolim.core.municipality.infrastructure.bus.KtormMunicipalityCommandHandlers
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.*
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormCommandBus(database: Database) : CommandBus {
    private val commandHandlers: Map<KClass<out Command>, CommandHandler<out Command>>

    init {
        this.commandHandlers = HashMap()
        commandHandlers.apply {
            putAll(KtormFederalEntityCommandHandlers(database))
            putAll(KtormInstrumentTypeCommandHandlers(database))
            putAll(KtormMunicipalityCommandHandlers(database))
        }
    }

    override fun dispatch(command: Command) {
        try {
            commandHandlers[command::class]?.let {
                @Suppress("UNCHECKED_CAST")
                it as CommandHandler<Command>
                it.handle(command)
            } ?: throw CommandNotRegisteredError(command::class)
        } catch (e: Exception) {
            throw CommandHandlerExecutionError(e)
        }
    }
}
