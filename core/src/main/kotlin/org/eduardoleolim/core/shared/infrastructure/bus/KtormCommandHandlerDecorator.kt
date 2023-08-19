package org.eduardoleolim.core.shared.infrastructure.bus

import org.eduardoleolim.shared.domain.bus.command.Command
import org.eduardoleolim.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.shared.domain.bus.command.CommandHandlerExecutionError
import org.ktorm.database.Database

class KtormCommandHandlerDecorator<T : Command>(
    private val database: Database,
    private val commandHandler: CommandHandler<T>
) :
    CommandHandler<T> {
    override fun handle(command: T) {
        try {
            database.useTransaction {
                commandHandler.handle(command)
            }
        } catch (e: Exception) {
            throw CommandHandlerExecutionError(e)
        }
    }
}