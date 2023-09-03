package org.eduardoleolim.core.instrumentType.domain

import org.eduardoleolim.shared.domain.criteria.*

object InstrumentTypeCriteria {
    fun idCriteria(id: String) = Criteria(
        Filters(listOf(Filter(FilterField("id"), FilterOperator.EQUAL, FilterValue(id)))),
        Filters.none(),
        Orders.none(),
        1,
        null
    )

    fun nameCriteria(name: String) = Criteria(
        Filters(listOf(Filter(FilterField("name"), FilterOperator.EQUAL, FilterValue(name)))),
        Filters.none(),
        Orders.none(),
        1,
        null
    )

    fun searchCriteria(search: String? = null, orders: List<Order>? = null, limit: Int? = null, offset: Int? = null) =
        Criteria(
            Filters.none(),
            Filters(
                search?.let {
                    listOf(Filter(FilterField("name"), FilterOperator.CONTAINS, FilterValue(it)))
                } ?: emptyList()
            ),
            Orders(orders ?: listOf(Order.asc("name"))),
            limit,
            offset
        )
}
