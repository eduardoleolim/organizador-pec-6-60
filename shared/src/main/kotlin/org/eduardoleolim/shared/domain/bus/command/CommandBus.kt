package org.eduardoleolim.shared.domain.bus.command

interface CommandBus {
    @Throws(CommandHandlerExecutionError::class)
    fun dispatch(command: Command)
}
