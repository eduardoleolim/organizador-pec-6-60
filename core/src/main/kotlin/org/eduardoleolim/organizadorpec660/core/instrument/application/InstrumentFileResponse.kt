package org.eduardoleolim.organizadorpec660.core.instrument.application

import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentFile

class InstrumentFileResponse(val id: String, val content: ByteArray) {
    companion object {
        fun fromAggregate(instrumentFile: InstrumentFile): InstrumentFileResponse {
            return InstrumentFileResponse(instrumentFile.id().toString(), instrumentFile.content())
        }
    }
}
