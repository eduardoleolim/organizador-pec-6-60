package org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command

import org.eduardoleolim.organizadorpec660.core.shared.domain.Either

interface CommandHandler<L, R, T : Command<L, R>> {
    fun handle(command: T): Either<L, R>
}
