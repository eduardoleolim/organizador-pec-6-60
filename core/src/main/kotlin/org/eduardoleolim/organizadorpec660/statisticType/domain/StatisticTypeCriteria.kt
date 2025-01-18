/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.organizadorpec660.statisticType.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*

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
                val fields = StatisticTypeFields.entries.map { it.value }
                val filteredOrders = orders.mapNotNull { it.takeIf { fields.contains(it["orderBy"]) } }

                Orders.fromValues(filteredOrders.toTypedArray())
            } ?: Orders(Order.asc(StatisticTypeFields.KeyCode.value)),
            limit,
            offset
        )
}
