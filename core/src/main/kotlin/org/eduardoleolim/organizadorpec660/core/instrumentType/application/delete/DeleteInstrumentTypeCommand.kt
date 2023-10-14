package org.eduardoleolim.organizadorpec660.core.instrumentType.application.delete

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command

class DeleteInstrumentTypeCommand(id: String) : Command {
    private val id = id.trim()

    fun id(): String {
        return id
    }
}
