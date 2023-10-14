package org.eduardoleolim.organizadorpec660.core.municipality.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*

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
        Filters(listOf(Filter(FilterField("federalEntity.id"), FilterOperator.EQUAL, FilterValue(federalEntityId)))),
        Filters.none(),
        Orders.none(),
        1,
        null
    )

    fun searchCriteria(
        federalEntityId: String? = null,
        search: String? = null,
        orders: List<Order>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = Criteria(
        Filters(
            federalEntityId?.let {
                listOf(
                    Filter(FilterField("federalEntity.id"), FilterOperator.EQUAL, FilterValue(it))
                )
            } ?: emptyList()
        ),
        Filters(
            search?.let {
                listOf(
                    Filter(FilterField("keyCode"), FilterOperator.CONTAINS, FilterValue(it)),
                    Filter(FilterField("name"), FilterOperator.CONTAINS, FilterValue(it)),
                    Filter(FilterField("federalEntity.keyCode"), FilterOperator.CONTAINS, FilterValue(it)),
                    Filter(FilterField("federalEntity.name"), FilterOperator.CONTAINS, FilterValue(it))
                )
            } ?: emptyList()
        ),
        Orders(orders ?: listOf(Order.asc("keyCode"))),
        limit,
        offset
    )
}
