package org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command

import kotlin.reflect.KClass

class CommandNotRegisteredError(command: KClass<out Command<*, *>>) :
    Exception("The command ${command.simpleName} hasn't a command handler associated")
