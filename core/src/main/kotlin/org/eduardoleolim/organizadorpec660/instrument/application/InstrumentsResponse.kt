package org.eduardoleolim.organizadorpec660.instrument.application

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response

class InstrumentsResponse(
    val instruments: List<InstrumentResponse>,
    val total: Int,
    val limit: Int?,
    val offset: Int?
) : Response {
    val filtered: Int
        get() = instruments.size
}
