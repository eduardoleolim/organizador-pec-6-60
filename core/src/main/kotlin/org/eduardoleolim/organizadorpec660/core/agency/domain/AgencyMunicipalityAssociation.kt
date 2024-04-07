package org.eduardoleolim.organizadorpec660.core.agency.domain

import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityId

class AgencyMunicipalityAssociation private constructor(
    private val agencyId: AgencyId,
    private val municipalityId: MunicipalityId,
    private var isOwner: Boolean
) {
    companion object {
        fun from(agencyId: String, municipalityId: String, isOwner: Boolean) = AgencyMunicipalityAssociation(
            AgencyId.fromString(agencyId),
            MunicipalityId.fromString(municipalityId),
            isOwner
        )
    }

    fun agencyId() = agencyId.value

    fun municipalityId() = municipalityId.value

    fun isOwner() = isOwner

    fun changeOwner(isOwner: Boolean) {
        this.isOwner = isOwner
    }
}
