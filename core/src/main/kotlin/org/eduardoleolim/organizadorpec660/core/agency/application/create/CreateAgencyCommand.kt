package org.eduardoleolim.organizadorpec660.core.agency.application.create

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import java.util.*

class CreateAgencyCommand(
    name: String,
    consecutive: String,
    private val municipalityId: String,
    statisticTypeIds: List<String>
) : Command<AgencyError, UUID> {
    private val name = name.trim().uppercase()
    private val consecutive = consecutive.trim()
    private val statisticTypeIds = statisticTypeIds.distinct()

    fun name(): String {
        return name
    }

    fun consecutive(): String {
        return consecutive
    }

    fun municipalityId(): String {
        return municipalityId
    }

    fun statisticTypeIds(): List<String> {
        return statisticTypeIds
    }
}
