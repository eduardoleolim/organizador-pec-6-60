package org.eduardoleolim.core.municipality.domain

import org.eduardoleolim.shared.domain.criteria.*

object MunicipalityCriteria {
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

    fun anotherKeyCodeCriteria(id: String, keyCode: String) = Criteria(
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

    fun federalEntityIdCriteria(federalEntityId: String) = Criteria(
        Filters(listOf(Filter(FilterField("federalEntityId"), FilterOperator.EQUAL, FilterValue(federalEntityId)))),
        Filters.none(),
        Orders.none(),
        1,
        null
    )
}
