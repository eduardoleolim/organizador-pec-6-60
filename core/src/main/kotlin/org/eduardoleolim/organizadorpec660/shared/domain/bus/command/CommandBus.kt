package org.eduardoleolim.organizadorpec660.shared.domain.bus.command

import arrow.core.Either

interface CommandBus {
    fun <L, R> dispatch(command: Command<L, R>): Either<L, R>
}
