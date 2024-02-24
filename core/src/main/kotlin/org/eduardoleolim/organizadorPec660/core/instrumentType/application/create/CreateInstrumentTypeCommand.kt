package org.eduardoleolim.organizadorPec660.core.instrumentType.application.create

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command

class CreateInstrumentTypeCommand(name: String) : Command {
    private val name = name.trim()

    fun name(): String {
        return name
    }
}