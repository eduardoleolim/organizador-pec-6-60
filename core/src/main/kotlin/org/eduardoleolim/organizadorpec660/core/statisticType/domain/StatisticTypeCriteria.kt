package org.eduardoleolim.organizadorpec660.core.statisticType.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*

enum class StatisticTypeFields(val value: String) {
    Id("id"),
    KeyCode("keyCode"),
    Name("name"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}

object StatisticTypeCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal(StatisticTypeFields.Id.value, id), Orders.none(), 1, null)

    fun keyCodeCriteria(keyCode: String) = Criteria(
        SingleFilter.equal(StatisticTypeFields.KeyCode.value, keyCode),
        Orders.none(),
        1,
        null
    )

    fun anotherSameKeyCodeCriteria(id: String, keyCode: String) = Criteria(
        AndFilters(
            SingleFilter.notEqual(StatisticTypeFields.Id.value, id),
            SingleFilter.equal(StatisticTypeFields.KeyCode.value, keyCode)
        ),
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
                OrFilters(
                    SingleFilter.contains(StatisticTypeFields.KeyCode.value, it),
                    SingleFilter.contains(StatisticTypeFields.Name.value, it)
                )
            } ?: EmptyFilters(),
            orders?.let {
                Orders.fromValues(orders)
            } ?: Orders(Order.asc(StatisticTypeFields.KeyCode.value)),
            limit,
            offset
        )
}
