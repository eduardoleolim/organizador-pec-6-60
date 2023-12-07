package org.eduardoleolim.organizadorpec660.core.statisticType.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*

object StatisticTypeCriteria {
    fun idCriteria(id: String) = Criteria(
        SingleFilter(Filter(FilterField("id"), FilterOperator.EQUAL, FilterValue(id))),
        Orders.none(),
        1,
        null
    )

    fun keyCodeCriteria(keyCode: String) = Criteria(
        SingleFilter(Filter(FilterField("keyCode"), FilterOperator.EQUAL, FilterValue(keyCode))),
        Orders.none(),
        1,
        null
    )

    fun anotherSameKeyCodeCriteria(id: String, keyCode: String) = Criteria(
        AndFilters(
            listOf(
                SingleFilter(Filter(FilterField("id"), FilterOperator.NOT_EQUAL, FilterValue(id))),
                SingleFilter(Filter(FilterField("keyCode"), FilterOperator.EQUAL, FilterValue(keyCode)))
            )
        ),
        Orders.none(),
        1,
        null
    )
}
