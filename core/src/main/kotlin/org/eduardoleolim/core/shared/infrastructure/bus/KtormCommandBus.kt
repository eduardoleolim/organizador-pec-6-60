package org.eduardoleolim.core.shared.infrastructure.bus

import org.eduardoleolim.core.federalEntity.infrastructure.bus.KtormFederalEntityCommandHandlers
import org.eduardoleolim.shared.domain.bus.command.*
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormCommandBus(database: Database) : CommandBus {
    private val commandHandlers: HashMap<KClass<Command>, CommandHandler<Command>> = HashMap()

    init {
        commandHandlers.apply {
            putAll(KtormFederalEntityCommandHandlers(database))
        }
    }

    override fun dispatch(command: Command) {
        try {
            commandHandlers[command::class]?.handle(command) ?: throw CommandNotRegisteredError(command::class)
        } catch (e: Exception) {
            throw CommandHandlerExecutionError(e)
        }
    }
}
