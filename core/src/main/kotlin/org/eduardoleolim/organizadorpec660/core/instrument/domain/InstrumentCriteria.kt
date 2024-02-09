package org.eduardoleolim.organizadorpec660.core.instrument.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.AndFilters
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Orders
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.SingleFilter

object InstrumentCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal("id", id), Orders.none(), 1, null)

    fun anotherInstrumentCriteria(
        instrumentId: String,
        statisticYear: Int,
        statisticMonth: Int,
        consecutive: String,
        instrumentTypeId: String,
        statisticTypeId: String,
        municipalityId: String
    ) = Criteria(
        AndFilters(
            listOf(
                SingleFilter.notEqual("id", instrumentId),
                SingleFilter.equal("statisticYear", statisticYear.toString()),
                SingleFilter.equal("statisticMonth", statisticMonth.toString()),
                SingleFilter.equal("consecutive", consecutive),
                SingleFilter.equal("instrumentTypeId", instrumentTypeId),
                SingleFilter.equal("statisticTypeId", statisticTypeId),
                SingleFilter.equal("municipalityId", municipalityId)
            )
        ),
        Orders.none(),
        1,
        null
    )
}
