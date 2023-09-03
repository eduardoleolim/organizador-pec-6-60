package org.eduardoleolim.core.instrumentType.application.delete

import org.eduardoleolim.shared.domain.bus.command.Command

class DeleteInstrumentTypeCommand(id: String) : Command {
    private val id = id.trim()

    fun id(): String {
        return id
    }
}
