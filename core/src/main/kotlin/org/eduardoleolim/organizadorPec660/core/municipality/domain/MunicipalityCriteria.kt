package org.eduardoleolim.organizadorPec660.core.municipality.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.*

object MunicipalityCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal("id", id), Orders.none(), 1, null)

    fun keyCodeAndFederalEntityIdCriteria(keyCode: String, federalEntityId: String) = Criteria(
        AndFilters(
            listOf(
                SingleFilter.equal("keyCode", keyCode),
                SingleFilter.equal("federalEntity.id", federalEntityId)
            )
        ),
        Orders.none(),
        1,
        null
    )

    fun anotherKeyCodeCriteria(id: String, keyCode: String) = Criteria(
        AndFilters(listOf(SingleFilter.notEqual("id", id), SingleFilter.equal("keyCode", keyCode))),
        Orders.none(),
        1,
        null
    )

    fun federalEntityIdCriteria(federalEntityId: String) = Criteria(
        SingleFilter.equal("federalEntity.id", federalEntityId),
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
                    SingleFilter.equal("federalEntity.id", it)
                } ?: EmptyFilters(),
                search?.let {
                    OrFilters(
                        listOf(
                            SingleFilter.contains("keyCode", it),
                            SingleFilter.contains("name", it),
                            SingleFilter.contains("federalEntity.keyCode", it),
                            SingleFilter.contains("federalEntity.name", it)
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
