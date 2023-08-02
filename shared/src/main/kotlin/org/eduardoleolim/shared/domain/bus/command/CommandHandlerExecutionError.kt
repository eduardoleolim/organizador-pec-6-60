package org.eduardoleolim.shared.domain.bus.command

class CommandHandlerExecutionError(cause: Throwable) : RuntimeException(cause)
