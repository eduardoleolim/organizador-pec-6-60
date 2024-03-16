package org.eduardoleolim.organizadorPec660.core.federalEntity.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.*

object FederalEntityCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal("id", id), Orders.none(), 1, null)

    fun keyCodeCriteria(keyCode: String) = Criteria(SingleFilter.equal("keyCode", keyCode), Orders.none(), 1, null)

    fun anotherKeyCodeCriteria(id: String, keyCode: String) = Criteria(
        AndFilters(listOf(SingleFilter.notEqual("id", id), SingleFilter.equal("keyCode", keyCode))),
        Orders.none(),
        1,
        null
    )

    fun searchCriteria(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) =
        Criteria(
            search?.let {
                OrFilters(listOf(SingleFilter.contains("keyCode", it), SingleFilter.contains("name", it)))
            } ?: EmptyFilters(),
            orders?.let {
                Orders.fromValues(orders)
            } ?: Orders(listOf(Order.asc("keyCode"))),
            limit,
            offset
        )
}
