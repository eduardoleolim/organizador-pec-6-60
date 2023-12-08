package org.eduardoleolim.organizadorpec660.core.shared.domain.criteria

class Order(val orderBy: OrderBy, val orderType: OrderType) {
    fun hasOrder() = !orderType.isNone()

    fun serialize() = String.format("%s.%s", orderBy.value, orderType.type)

    companion object {
        fun fromValues(orderBy: String?, orderType: String?) =
            if (orderBy == null) none() else Order(OrderBy(orderBy), OrderType.valueOf(orderType ?: "asc"))

        fun none() = Order(OrderBy(""), OrderType.NONE)

        fun desc(orderBy: String) = Order(OrderBy(orderBy), OrderType.DESC)

        fun asc(orderBy: String) = Order(OrderBy(orderBy), OrderType.ASC)
    }
}
