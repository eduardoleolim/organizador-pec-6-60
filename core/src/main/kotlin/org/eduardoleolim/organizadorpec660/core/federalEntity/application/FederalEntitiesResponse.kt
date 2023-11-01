package org.eduardoleolim.organizadorpec660.core.federalEntity.application

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response

class FederalEntitiesResponse(private val federalEntities: List<FederalEntityResponse>) : Response {
    fun federalEntities() = federalEntities

    companion object {
        fun fromAggregate(federalEntities: List<FederalEntity>) =
            FederalEntitiesResponse(federalEntities.map(FederalEntityResponse::fromAggregate))
    }

}