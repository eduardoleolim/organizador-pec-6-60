package org.eduardoleolim.organizadorpec660.core.agency.application.update

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class UpdateAgencyCommand(
    agencyId: String,
    name: String,
    consecutive: String,
    municipalityId: String,
    statisticTypeIds: List<String>
) : Command<AgencyError, Unit> {
    private val agencyId = agencyId.trim()
    private val name = name.trim().uppercase()
    private val consecutive = consecutive.trim()
    private val municipalityId = municipalityId.trim()
    private val statisticTypeIds = statisticTypeIds.distinct()

    fun agencyId(): String {
        return agencyId
    }

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
