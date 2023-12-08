package org.eduardoleolim.organizadorpec660.core.federalEntity.application

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import java.util.*

class FederalEntityResponse(
    val id: String,
    val keyCode: String,
    val name: String,
    val createdAt: Date,
    val updatedAt: Date?
) : Response {
    companion object {
        fun fromAggregate(federalEntity: FederalEntity): FederalEntityResponse {
            return FederalEntityResponse(
                federalEntity.id().toString(),
                federalEntity.keyCode(),
                federalEntity.name(),
                federalEntity.createdAt(),
                federalEntity.updatedAt()
            )
        }
    }
}
