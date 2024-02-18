package org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command

interface CommandHandler<T : Command> {
    fun handle(command: T)
}
