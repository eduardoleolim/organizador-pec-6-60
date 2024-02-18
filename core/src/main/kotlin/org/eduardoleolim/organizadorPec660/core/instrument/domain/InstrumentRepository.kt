package org.eduardoleolim.organizadorPec660.core.instrument.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

interface InstrumentRepository {
    fun matching(criteria: Criteria): List<Instrument>

    fun count(criteria: Criteria): Int

    fun save(instrument: Instrument)

    fun delete(instrumentId: String)
}
