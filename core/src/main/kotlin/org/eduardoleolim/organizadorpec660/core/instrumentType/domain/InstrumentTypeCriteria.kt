package org.eduardoleolim.organizadorpec660.core.instrumentType.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*

object InstrumentTypeCriteria {
    fun idCriteria(id: String) = Criteria(
        SingleFilter(Filter(FilterField("id"), FilterOperator.EQUAL, FilterValue(id))),
        Orders.none(),
        1,
        null
    )

    fun nameCriteria(name: String) = Criteria(
        SingleFilter(Filter(FilterField("name"), FilterOperator.EQUAL, FilterValue(name))),
        Orders.none(),
        1,
        null
    )

    fun searchCriteria(search: String? = null, orders: List<Order>? = null, limit: Int? = null, offset: Int? = null) =
        Criteria(
            search?.let {
                OrFilters(
                    listOf(
                        SingleFilter(Filter(FilterField("name"), FilterOperator.CONTAINS, FilterValue(it)))
                    )
                )
            } ?: EmptyFilters(),
            Orders(orders ?: listOf(Order.asc("name"))),
            limit,
            offset
        )
}
