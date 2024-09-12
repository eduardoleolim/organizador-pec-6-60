package org.eduardoleolim.organizadorpec660.instrument.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

interface InstrumentRepository {
    fun matching(criteria: Criteria): List<Instrument>

    fun searchFileById(id: String): InstrumentFile?

    fun searchInstrumentFile(instrumentFileId: String): InstrumentFile?

    fun count(criteria: Criteria): Int

    fun save(instrument: Instrument, instrumentFile: InstrumentFile? = null)

    fun delete(instrumentId: String)
}
