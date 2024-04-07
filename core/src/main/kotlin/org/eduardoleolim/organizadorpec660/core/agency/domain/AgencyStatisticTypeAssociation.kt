package org.eduardoleolim.organizadorpec660.core.agency.domain

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeId
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeId

class AgencyStatisticTypeAssociation private constructor(
    private val agencyId: AgencyId,
    private val statisticTypeId: StatisticTypeId,
    private var instrumentTypeId: InstrumentTypeId
) {
    companion object {
        fun from(agencyId: String, statisticTypeId: String, instrumentTypeId: String) = AgencyStatisticTypeAssociation(
            AgencyId.fromString(agencyId),
            StatisticTypeId.fromString(statisticTypeId),
            InstrumentTypeId.fromString(instrumentTypeId)
        )
    }

    fun agencyId() = agencyId.value

    fun statisticTypeId() = statisticTypeId.value

    fun instrumentTypeId() = instrumentTypeId.value

    fun changeInstrumentTypeId(instrumentTypeId: String) {
        this.instrumentTypeId = InstrumentTypeId.fromString(instrumentTypeId)
    }
}
