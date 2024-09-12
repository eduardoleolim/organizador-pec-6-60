package org.eduardoleolim.organizadorpec660.shared.domain.bus.command

import org.eduardoleolim.organizadorpec660.shared.domain.Either

interface CommandHandler<L, R, T : Command<L, R>> {
    fun handle(command: T): Either<L, R>
}
