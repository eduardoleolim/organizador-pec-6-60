package org.eduardoleolim.organizadorpec660.shared.domain.bus.command

interface CommandBus {
    @Throws(CommandHandlerExecutionError::class)
    fun dispatch(command: Command)
}
