package org.eduardoleolim.organizadorpec660.core.federalEntity.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*

enum class FederalEntityFields(val value: String) {
    Id("id"),
    KeyCode("keyCode"),
    Name("name"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}

object FederalEntityCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal(FederalEntityFields.Id.value, id), Orders.none(), 1, null)

    fun keyCodeCriteria(keyCode: String) =
        Criteria(SingleFilter.equal(FederalEntityFields.KeyCode.value, keyCode), Orders.none(), 1, null)

    fun anotherKeyCodeCriteria(id: String, keyCode: String) = Criteria(
        AndFilters(
            SingleFilter.notEqual(FederalEntityFields.Id.value, id),
            SingleFilter.equal(FederalEntityFields.KeyCode.value, keyCode)
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
                    SingleFilter.contains(FederalEntityFields.KeyCode.value, it),
                    SingleFilter.contains(FederalEntityFields.Name.value, it)
                )
            } ?: EmptyFilters(),
            orders?.let {
                Orders.fromValues(orders)
            } ?: Orders(listOf(Order.asc(FederalEntityFields.KeyCode.value))),
            limit,
            offset
        )
}
