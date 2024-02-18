package org.eduardoleolim.organizadorPec660.core.instrumentType.application.update

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command

class UpdateInstrumentTypeCommand(id: String, name: String) : Command {
    private val id: String = id.trim()
    private val name: String = name.trim().uppercase()

    fun id(): String {
        return id
    }

    fun name(): String {
        return name
    }
}
