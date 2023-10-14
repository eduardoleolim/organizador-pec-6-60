package org.eduardoleolim.organizadorpec660.core.instrumentType.application

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response

class InstrumentTypesResponse(private val instrumentTypes: List<InstrumentTypeResponse>) : Response {
    fun instrumentTypes() = instrumentTypes

    companion object {
        fun fromAggregate(instrumentTypes: List<InstrumentType>) =
            InstrumentTypesResponse(instrumentTypes.map(InstrumentTypeResponse::fromAggregate))
    }
}
