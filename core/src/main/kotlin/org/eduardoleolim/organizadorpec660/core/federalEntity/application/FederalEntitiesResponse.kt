package org.eduardoleolim.organizadorpec660.core.federalEntity.application

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response

class FederalEntitiesResponse(val federalEntities: List<FederalEntityResponse>, val totalRecords: Int) : Response {

    companion object {
        fun fromAggregate(federalEntities: List<FederalEntity>, totalRecords: Int) =
            FederalEntitiesResponse(federalEntities.map(FederalEntityResponse::fromAggregate), totalRecords)
    }

}
