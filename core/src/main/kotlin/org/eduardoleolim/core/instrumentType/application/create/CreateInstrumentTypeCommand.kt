package org.eduardoleolim.core.instrumentType.application.create

import org.eduardoleolim.shared.domain.bus.command.Command

class CreateInstrumentTypeCommand(nombre: String) : Command {
    private val nombre = nombre.trim()

    fun nombre(): String {
        return nombre
    }
}
