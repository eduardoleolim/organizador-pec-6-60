package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.ktorm.database.Database

class KtormCommandHandlerDecorator<T : Command>(
    private val database: Database,
    private val commandHandler: CommandHandler<T>
) : CommandHandler<T> {
    override fun handle(command: T) {
        database.useTransaction {
            commandHandler.handle(command)
        }
    }
}
