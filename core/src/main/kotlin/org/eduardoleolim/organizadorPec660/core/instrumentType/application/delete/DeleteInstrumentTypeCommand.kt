package org.eduardoleolim.organizadorPec660.core.instrumentType.application.delete

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command

class DeleteInstrumentTypeCommand(id: String) : Command {
    private val id = id.trim()

    fun id(): String {
        return id
    }
}
