package org.eduardoleolim.organizadorpec660.core.municipality.application

import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import java.util.*

class SimpleMunicipalityResponse(
    val id: String,
    val name: String,
    val keyCode: String,
    val federalEntityId: String,
    val createdAt: Date,
    val updatedAt: Date?
) {
    companion object {
        fun fromAggregate(municipality: Municipality) = SimpleMunicipalityResponse(
            municipality.id().toString(),
            municipality.name(),
            municipality.keyCode(),
            municipality.federalEntityId().toString(),
            municipality.createdAt(),
            municipality.updatedAt()
        )
    }
}
