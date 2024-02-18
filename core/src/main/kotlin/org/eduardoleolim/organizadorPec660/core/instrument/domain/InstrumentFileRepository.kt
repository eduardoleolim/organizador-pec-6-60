package org.eduardoleolim.organizadorPec660.core.instrument.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

interface InstrumentFileRepository {
    /**
     * Find the [InstrumentFile] that matches the provided [Criteria].
     *
     * @param criteria The [Criteria] to match.
     * @return The [InstrumentFile] that matches the provided [Criteria], or null if none is found.
     */
    fun first(criteria: Criteria): InstrumentFile?

    /**
     * Save the provided [InstrumentFile].
     *
     * @param instrumentFile The [InstrumentFile] to save.
     * @throws InstrumentNotFoundError If there is no saved [Instrument] associated with the provided [InstrumentId] for the [InstrumentFile].
     */
    fun save(instrumentFile: InstrumentFile)
}
