package org.eduardoleolim.core.municipality.application

import org.eduardoleolim.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.core.municipality.domain.Municipality
import org.eduardoleolim.shared.domain.bus.query.Response

class MunicipalityResponse(
    val id: String,
    val name: String,
    val keyCode: String,
    val federalEntity: FederalEntityResponse
) : Response {
    companion object {
        fun fromAggregate(municipality: Municipality, federalEntity: FederalEntity) = MunicipalityResponse(
            municipality.id().toString(),
            municipality.name(),
            municipality.keyCode(),
            FederalEntityResponse.fromAggregate(federalEntity)
        )
    }
}
