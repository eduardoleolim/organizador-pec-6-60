package org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command

class CommandHandlerExecutionError(cause: Throwable) : RuntimeException(cause)
