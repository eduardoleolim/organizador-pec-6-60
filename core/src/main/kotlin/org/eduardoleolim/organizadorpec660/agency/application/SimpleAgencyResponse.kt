package org.eduardoleolim.organizadorpec660.agency.application

import org.eduardoleolim.organizadorpec660.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import java.util.*

class SimpleAgencyResponse(
    val id: String,
    val name: String,
    val consecutive: String,
    val createdAt: Date,
    val updatedAt: Date?
) : Response {
    companion object {
        fun fromAggregate(agency: Agency): SimpleAgencyResponse {
            return SimpleAgencyResponse(
                agency.id().toString(),
                agency.name(),
                agency.consecutive(),
                agency.createdAt(),
                agency.updatedAt()
            )
        }
    }
}
