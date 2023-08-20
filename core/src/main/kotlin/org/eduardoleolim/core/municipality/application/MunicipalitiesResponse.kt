package org.eduardoleolim.core.municipality.application

import org.eduardoleolim.shared.domain.bus.query.Response

class MunicipalitiesResponse(private val municipalities: List<MunicipalityResponse>) : Response {
    fun municipalities() = municipalities
}
