package org.eduardoleolim.core.federalEntity.domain

import org.eduardoleolim.shared.domain.criteria.*

object FederalEntityCriteria {
    fun idCriteria(id: String) = Criteria(
        Filters(listOf(Filter(FilterField("id"), FilterOperator.EQUAL, FilterValue(id)))),
        Filters.none(),
        Order.none(),
        1,
        null
    )

    fun keyCodeCriteria(keyCode: String) = Criteria(
        Filters(listOf(Filter(FilterField("keyCode"), FilterOperator.EQUAL, FilterValue(keyCode)))),
        Filters.none(),
        Order.none(),
        1,
        null
    )
}
