package org.eduardoleolim.organizadorpec660.shared.domain.criteria

class Orders(val orders: List<Order>) {
    fun serialize() = orders.joinToString(separator = "^", transform = Order::serialize)

    companion object {
        fun none() = Orders(emptyList())

        fun fromValues(orders: Array<HashMap<String, String>>) =
            Orders(orders.map { Order.fromValues(it["orderBy"], it["orderType"]) })
    }
}
