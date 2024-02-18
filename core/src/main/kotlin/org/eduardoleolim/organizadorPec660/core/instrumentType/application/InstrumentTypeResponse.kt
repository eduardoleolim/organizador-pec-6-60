package org.eduardoleolim.organizadorPec660.core.instrumentType.application

import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import java.util.*

class InstrumentTypeResponse(val id: String, val name: String, val createdAt: Date, val updatedAt: Date?) : Response {
    companion object {
        fun fromAggregate(instrumentType: InstrumentType): InstrumentTypeResponse {
            return InstrumentTypeResponse(
                instrumentType.id().toString(),
                instrumentType.name(),
                instrumentType.createdAt(),
                instrumentType.updatedAt()
            )
        }
    }
}
