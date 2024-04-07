package org.eduardoleolim.organizadorpec660.core.municipality.application

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response

class MunicipalitiesResponse(
    val municipalities: List<MunicipalityResponse>,
    val total: Int,
    val limit: Int?,
    val offset: Int?
) : Response {
    val filtered: Int
        get() = municipalities.size
}
