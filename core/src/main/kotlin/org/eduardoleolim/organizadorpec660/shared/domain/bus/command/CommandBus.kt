package org.eduardoleolim.organizadorpec660.shared.domain.bus.command

import org.eduardoleolim.organizadorpec660.shared.domain.Either

interface CommandBus {
    fun <L, R> dispatch(command: Command<L, R>): Either<L, R>
}
