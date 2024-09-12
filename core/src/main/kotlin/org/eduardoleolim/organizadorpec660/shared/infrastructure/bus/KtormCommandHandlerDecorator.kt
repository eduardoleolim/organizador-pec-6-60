package org.eduardoleolim.organizadorpec660.shared.infrastructure.bus

import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.shared.domain.onLeft
import org.ktorm.database.Database

class KtormCommandHandlerDecorator<L, R, T : Command<L, R>>(
    private val database: Database,
    private val commandHandler: CommandHandler<L, R, T>
) : CommandHandler<L, R, T> {
    override fun handle(command: T): Either<L, R> {
        return database.useTransaction { transaction ->
            commandHandler.handle(command).onLeft {
                transaction.rollback()
            }
        }
    }
}
