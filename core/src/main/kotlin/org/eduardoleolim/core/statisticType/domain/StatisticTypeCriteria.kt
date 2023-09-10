package org.eduardoleolim.core.statisticType.domain

import org.eduardoleolim.shared.domain.criteria.*

object StatisticTypeCriteria {
    fun keyCodeCriteria(keyCode: String) = Criteria(
        Filters(listOf(Filter(FilterField("keyCode"), FilterOperator.EQUAL, FilterValue(keyCode)))),
        Filters.none(),
        Orders.none(),
        1,
        null
    )
}
