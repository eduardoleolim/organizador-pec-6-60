package org.eduardoleolim.organizadorpec660.shared.domain.bus.command

interface CommandHandler<T : Command> {
    fun handle(command: T)
}
