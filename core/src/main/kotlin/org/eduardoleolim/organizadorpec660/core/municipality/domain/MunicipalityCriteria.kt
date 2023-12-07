package org.eduardoleolim.organizadorpec660.core.municipality.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*

object MunicipalityCriteria {
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

    fun anotherKeyCodeCriteria(id: String, keyCode: String) = Criteria(
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

    fun federalEntityIdCriteria(federalEntityId: String) = Criteria(
        SingleFilter(Filter(FilterField("federalEntity.id"), FilterOperator.EQUAL, FilterValue(federalEntityId))),
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
        AndFilters(
            listOf(
                federalEntityId?.let {
                    SingleFilter(Filter(FilterField("federalEntity.id"), FilterOperator.EQUAL, FilterValue(it)))
                } ?: EmptyFilters(),
                search?.let {
                    OrFilters(
                        listOf(
                            SingleFilter(Filter(FilterField("keyCode"), FilterOperator.CONTAINS, FilterValue(it))),
                            SingleFilter(Filter(FilterField("name"), FilterOperator.CONTAINS, FilterValue(it))),
                            SingleFilter(
                                Filter(
                                    FilterField("federalEntity.keyCode"),
                                    FilterOperator.CONTAINS,
                                    FilterValue(it)
                                )
                            ),
                            SingleFilter(
                                Filter(
                                    FilterField("federalEntity.name"),
                                    FilterOperator.CONTAINS,
                                    FilterValue(it)
                                )
                            )
                        )
                    )
                } ?: EmptyFilters()
            )
        ),
        Orders(orders ?: listOf(Order.asc("keyCode"))),
        limit,
        offset
    )
}
