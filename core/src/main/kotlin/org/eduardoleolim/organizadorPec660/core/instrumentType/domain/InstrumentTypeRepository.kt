package org.eduardoleolim.organizadorPec660.core.instrumentType.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

interface InstrumentTypeRepository {
    fun matching(criteria: Criteria): List<InstrumentType>

    fun count(criteria: Criteria): Int

    fun save(instrumentType: InstrumentType)

    fun delete(instrumentTypeId: String)
}
