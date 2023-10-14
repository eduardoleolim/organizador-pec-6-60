package org.eduardoleolim.organizadorpec660.core.statisticType.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*

object StatisticTypeCriteria {
    fun idCriteria(id: String) = Criteria(
        Filters(listOf(Filter(FilterField("id"), FilterOperator.EQUAL, FilterValue(id)))),
        Filters.none(),
        Orders.none(),
        1,
        null
    )

    fun keyCodeCriteria(keyCode: String) = Criteria(
        Filters(listOf(Filter(FilterField("keyCode"), FilterOperator.EQUAL, FilterValue(keyCode)))),
        Filters.none(),
        Orders.none(),
        1,
        null
    )

    fun anotherSameKeyCodeCriteria(id: String, keyCode: String) = Criteria(
        Filters(
            listOf(
                Filter(FilterField("id"), FilterOperator.NOT_EQUAL, FilterValue(id)),
                Filter(FilterField("keyCode"), FilterOperator.EQUAL, FilterValue(keyCode))
            )
        ),
        Filters.none(),
        Orders.none(),
        1,
        null
    )
}
