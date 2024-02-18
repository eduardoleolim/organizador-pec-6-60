package org.eduardoleolim.organizadorPec660.core.instrumentType.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.*

object InstrumentTypeCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal("id", id), Orders.none(), 1, null)

    fun nameCriteria(name: String) = Criteria(SingleFilter.equal("name", name), Orders.none(), 1, null)

    fun searchCriteria(search: String? = null, orders: List<Order>? = null, limit: Int? = null, offset: Int? = null) =
        Criteria(
            search?.let {
                OrFilters(listOf(SingleFilter.contains("name", it)))
            } ?: EmptyFilters(),
            Orders(orders ?: listOf(Order.asc("name"))),
            limit,
            offset
        )
}
