package org.eduardoleolim.core.instrumentType.domain

import org.eduardoleolim.shared.domain.criteria.*

object InstrumentTypeCriteria {
    fun nameCriteria(name: String) = Criteria(
        Filters(listOf(Filter(FilterField("name"), FilterOperator.EQUAL, FilterValue(name)))),
        Filters.none(),
        Orders.none(),
        1,
        null
    )
}
