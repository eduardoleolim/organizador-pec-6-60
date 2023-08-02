package org.eduardoleolim.shared.domain.criteria

class Criteria(
    val andFilters: Filters,
    val orFilters: Filters,
    val order: Order,
    val limit: Int?,
    val offset: Int?,
    val isOrCriteria: Boolean = false
) {
    fun hasAndFilters() = andFilters.filters.isNotEmpty()

    fun hasOrFilters() = orFilters.filters.isNotEmpty()

    fun serialize() = String.format(
        "%s~~%s~~%s~~%s~~%s~~%s",
        andFilters.serialize(),
        orFilters.serialize(),
        order.serialize(),
        limit ?: 0,
        offset ?: 0,
        isOrCriteria
    )
}
