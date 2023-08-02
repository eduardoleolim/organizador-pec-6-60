package org.eduardoleolim.shared.domain.bus.command

interface CommandHandler<T : Command> {
    fun handle(command: T)
}
