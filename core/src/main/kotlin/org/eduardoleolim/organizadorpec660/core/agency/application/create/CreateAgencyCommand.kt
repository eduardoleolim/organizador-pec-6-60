package org.eduardoleolim.organizadorpec660.core.agency.application.create

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class CreateAgencyCommand(
    name: String,
    consecutive: Int,
    municipalities: List<Pair<String, Boolean>>,
    statisticTypes: List<Pair<String, String>>
) : Command {
    private val name = name.trim().uppercase()
    private val consecutive = consecutive
    private val municipalities = municipalities.distinctBy { it.first }
    private val statisticTypes = statisticTypes.distinctBy { it.first }

    fun name(): String {
        return name
    }

    fun consecutive(): Int {
        return consecutive
    }

    fun municipalities(): List<Pair<String, Boolean>> {
        return municipalities
    }

    fun statisticTypes(): List<Pair<String, String>> {
        return statisticTypes
    }
}
