package org.eduardoleolim.organizadorpec660.shared.domain.criteria

open class Criteria(
    val andFilters: Filters,
    val orFilters: Filters,
    val orders: Orders,
    val limit: Int?,
    val offset: Int?,
    val isOrCriteria: Boolean = false
) {
    fun hasAndFilters() = andFilters.filters.isNotEmpty()

    fun hasOrFilters() = orFilters.filters.isNotEmpty()

    fun hasOrders() = orders.orders.isNotEmpty()

    fun serialize() = String.format(
        "%s~~%s~~%s~~%s~~%s~~%s",
        andFilters.serialize(),
        orFilters.serialize(),
        orders.serialize(),
        limit ?: 0,
        offset ?: 0,
        isOrCriteria
    )
}
