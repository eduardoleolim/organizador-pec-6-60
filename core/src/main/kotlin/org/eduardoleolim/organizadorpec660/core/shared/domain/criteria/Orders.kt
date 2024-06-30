package org.eduardoleolim.organizadorpec660.core.shared.domain.criteria

class Orders(val orders: List<Order>) {
    constructor(vararg orders: Order) : this(orders.toList())

    fun serialize() = orders.joinToString(separator = "^", transform = Order::serialize)

    companion object {
        fun none() = Orders(emptyList())

        fun fromValues(orders: Array<HashMap<String, String>>) =
            Orders(orders.map { Order.fromValues(it["orderBy"], it["orderType"]) })
    }
}
