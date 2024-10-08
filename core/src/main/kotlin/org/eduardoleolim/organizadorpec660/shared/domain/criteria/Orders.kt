/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.shared.domain.criteria

class Orders(val orders: List<Order>) {
    constructor(vararg orders: Order) : this(orders.toList())

    fun serialize() = orders.joinToString(separator = "^", transform = Order::serialize)

    companion object {
        fun none() = Orders(emptyList())

        fun fromValues(orders: Array<HashMap<String, String>>) =
            Orders(orders.map { Order.fromValues(it["orderBy"], it["orderType"]) })
    }
}
