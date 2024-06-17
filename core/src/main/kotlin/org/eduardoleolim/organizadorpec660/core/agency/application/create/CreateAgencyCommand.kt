package org.eduardoleolim.organizadorpec660.core.agency.application.create

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class CreateAgencyCommand(
    name: String,
    private val consecutive: Int,
    private val municipalityId: String,
    statisticTypeIds: List<String>
) : Command {
    private val name = name.trim().uppercase()
    private val statisticTypeIds = statisticTypeIds.distinct()

    fun name(): String {
        return name
    }

    fun consecutive(): Int {
        return consecutive
    }

    fun municipalityId(): String {
        return municipalityId
    }

    fun statisticTypeIds(): List<String> {
        return statisticTypeIds
    }
}
