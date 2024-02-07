package org.eduardoleolim.organizadorpec660.core.federalEntity.application

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response

class FederalEntitiesResponse(
    val federalEntities: List<FederalEntityResponse>,
    val totalRecords: Int,
    val limit: Int?,
    val offset: Int?
) : Response {
    val filteredRecords: Int
        get() = federalEntities.size

    companion object {
        fun fromAggregate(federalEntities: List<FederalEntity>, totalRecords: Int, limit: Int?, offset: Int?) =
            FederalEntitiesResponse(
                federalEntities.map(FederalEntityResponse::fromAggregate),
                totalRecords,
                limit,
                offset
            )
    }

}
