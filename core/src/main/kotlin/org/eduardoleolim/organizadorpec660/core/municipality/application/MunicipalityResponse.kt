package org.eduardoleolim.organizadorpec660.core.municipality.application

import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import java.util.*


class MunicipalityResponse(
    val id: String,
    val name: String,
    val keyCode: String,
    val federalEntity: FederalEntityResponse,
    val createdAt: Date,
    val updatedAt: Date?
) : Response {
    companion object {
        fun fromAggregate(municipality: Municipality, federalEntity: FederalEntity) = MunicipalityResponse(
            municipality.id().toString(),
            municipality.name(),
            municipality.keyCode(),
            FederalEntityResponse.fromAggregate(federalEntity),
            municipality.createdAt(),
            municipality.updatedAt()
        )
    }
}