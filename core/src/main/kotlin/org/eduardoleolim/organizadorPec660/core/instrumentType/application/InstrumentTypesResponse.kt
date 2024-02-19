package org.eduardoleolim.organizadorPec660.core.instrumentType.application

import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response

class InstrumentTypesResponse(
    val instrumentTypes: List<InstrumentTypeResponse>,
    val total: Int,
    val limit: Int?,
    val offset: Int?
) : Response {
    val filtered: Int
        get() = instrumentTypes.size

    companion object {
        fun fromAggregate(instrumentTypes: List<InstrumentType>, total: Int, limit: Int?, offset: Int?) =
            InstrumentTypesResponse(instrumentTypes.map(InstrumentTypeResponse::fromAggregate), total, limit, offset)
    }
}
