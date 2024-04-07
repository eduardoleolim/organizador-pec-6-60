package org.eduardoleolim.organizadorpec660.core.shared.domain.criteria

open class Criteria(
    val filters: Filters,
    val orders: Orders,
    val limit: Int?,
    val offset: Int?
) {
    fun hasFilters() = when (filters) {
        is SingleFilter -> true
        is MultipleFilters -> filters.filters.isNotEmpty()
        is EmptyFilters -> false
    }

    fun hasOrders() = orders.orders.isNotEmpty()

    fun serialize() = String.format(
        "%s~~%s~~%s~~%s",
        filters.serialize(),
        orders.serialize(),
        limit ?: 0,
        offset ?: 0,
    )
}
