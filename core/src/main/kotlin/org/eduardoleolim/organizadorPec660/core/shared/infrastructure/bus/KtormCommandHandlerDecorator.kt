package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.loadExtensions
import org.ktorm.database.Database

class KtormCommandHandlerDecorator<T : Command>(
    private val database: Database,
    private val commandHandler: CommandHandler<T>,
    private val sqliteExtensions: List<String> = emptyList()
) : CommandHandler<T> {
    override fun handle(command: T) {
        database.useTransaction {
            it.connection.loadExtensions(sqliteExtensions)
            commandHandler.handle(command)
        }
    }
}
