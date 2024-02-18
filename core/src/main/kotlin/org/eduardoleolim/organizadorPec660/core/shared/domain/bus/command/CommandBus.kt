package org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command

interface CommandBus {
    @Throws(CommandHandlerExecutionError::class)
    fun dispatch(command: Command)
}
