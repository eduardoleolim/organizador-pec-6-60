package org.eduardoleolim.organizadorpec660.core.municipality.application

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response

class MunicipalitiesResponse(private val municipalities: List<MunicipalityResponse>) : Response {
    fun municipalities() = municipalities
}
