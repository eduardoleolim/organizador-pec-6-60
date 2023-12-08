package org.eduardoleolim.organizadorpec660.core.instrumentType.application.create

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class CreateInstrumentTypeCommand(nombre: String) : Command {
    private val nombre = nombre.trim()

    fun nombre(): String {
        return nombre
    }
}
