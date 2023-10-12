package org.eduardoleolim.organizadorpec660.shared.domain.bus.command

class CommandHandlerExecutionError(cause: Throwable) : RuntimeException(cause)
