package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.ktorm.database.Database

class KtormCommandHandlerDecorator<L, R, T : Command<L, R>>(
    private val database: Database,
    private val commandHandler: CommandHandler<L, R, T>
) : CommandHandler<L, R, T> {
    override fun handle(command: T): Either<L, R> {
        return database.useTransaction {
            commandHandler.handle(command)
        }
    }
}
