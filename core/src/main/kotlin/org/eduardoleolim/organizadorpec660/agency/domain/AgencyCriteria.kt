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

package org.eduardoleolim.organizadorpec660.agency.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*

enum class AgencyFields(val value: String) {
    Id("id"),
    Name("name"),
    Consecutive("consecutive"),
    MunicipalityId("municipality.id"),
    MunicipalityKeyCode("municipality.keyCode"),
    MunicipalityName("municipality.name"),
    StatisticTypeId("statisticType.id"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}


object AgencyCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal(AgencyFields.Id.value, id), Orders.none(), 1, null)

    fun anotherConsecutiveCriteria(consecutive: String, municipalityId: String) = Criteria(
        AndFilters(
            SingleFilter.equal(AgencyFields.Consecutive.value, consecutive),
            SingleFilter.equal(AgencyFields.MunicipalityId.value, municipalityId)
        ),
        Orders.none(),
        1,
        null
    )

    fun anotherConsecutiveCriteria(agencyId: String, consecutive: String, municipalityOwnerId: String) = Criteria(
        AndFilters(
            SingleFilter.notEqual(AgencyFields.Id.value, agencyId),
            SingleFilter.equal(AgencyFields.Consecutive.value, consecutive),
            SingleFilter.equal(AgencyFields.MunicipalityId.value, municipalityOwnerId)
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
    ) = Criteria(
        search?.let {
            OrFilters(
                SingleFilter.contains(AgencyFields.Name.value, it),
                SingleFilter.contains(AgencyFields.Consecutive.value, it),
                SingleFilter.contains(AgencyFields.MunicipalityName.value, it),
                SingleFilter.contains(AgencyFields.MunicipalityKeyCode.value, it)
            )
        } ?: EmptyFilters(),
        orders?.let {
            val fields = AgencyFields.entries.map { it.value }
            val filteredOrders = orders.mapNotNull { it.takeIf { fields.contains(it["orderBy"]) } }

            Orders.fromValues(filteredOrders.toTypedArray())
        } ?: Orders(Order.asc(AgencyFields.Name.value)),
        limit,
        offset
    )

    fun statisticTypeIdCriteria(statisticTypeId: String) = Criteria(
        SingleFilter.equal(AgencyFields.StatisticTypeId.value, statisticTypeId),
        Orders.none(),
        null,
        null
    )

    fun municipalityIdCriteria(municipalityId: String) = Criteria(
        SingleFilter.equal(AgencyFields.MunicipalityId.value, municipalityId),
        Orders(Order.asc(AgencyFields.Consecutive.value)),
        null,
        null
    )
}
