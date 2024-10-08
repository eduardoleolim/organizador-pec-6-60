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

package org.eduardoleolim.organizadorpec660.municipality.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*

enum class MunicipalityFields(val value: String) {
    Id("id"),
    KeyCode("keyCode"),
    Name("name"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt"),
    FederalEntityId("federalEntity.id"),
    FederalEntityKeyCode("federalEntity.keyCode"),
    FederalEntityName("federalEntity.name"),
    FederalEntityCreatedAt("federalEntity.createdAt"),
    FederalEntityUpdatedAt("federalEntity.updatedAt")
}

object MunicipalityCriteria {
    fun idCriteria(id: String) =
        Criteria(SingleFilter.equal(MunicipalityFields.Id.value, id), Orders.none(), 1, null)

    fun keyCodeAndFederalEntityIdCriteria(keyCode: String, federalEntityId: String) = Criteria(
        AndFilters(
            SingleFilter.equal(MunicipalityFields.KeyCode.value, keyCode),
            SingleFilter.equal(MunicipalityFields.FederalEntityId.value, federalEntityId)
        ),
        Orders.none(),
        1,
        null
    )

    fun anotherKeyCodeCriteria(municipalityId: String, keyCode: String, federalEntityId: String) = Criteria(
        AndFilters(
            SingleFilter.notEqual(MunicipalityFields.Id.value, municipalityId),
            SingleFilter.equal(MunicipalityFields.KeyCode.value, keyCode),
            SingleFilter.equal(MunicipalityFields.FederalEntityId.value, federalEntityId)
        ),
        Orders.none(),
        1,
        null
    )

    fun federalEntityIdCriteria(federalEntityId: String) = Criteria(
        SingleFilter.equal(MunicipalityFields.FederalEntityId.value, federalEntityId),
        Orders.none(),
        1,
        null
    )

    fun searchCriteria(
        federalEntityId: String? = null,
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = Criteria(
        AndFilters(
            listOf(
                federalEntityId?.let {
                    SingleFilter.equal(MunicipalityFields.FederalEntityId.value, it)
                } ?: EmptyFilters(),
                search?.let {
                    OrFilters(
                        SingleFilter.contains(MunicipalityFields.KeyCode.value, it),
                        SingleFilter.contains(MunicipalityFields.Name.value, it),
                        SingleFilter.contains(MunicipalityFields.FederalEntityKeyCode.value, it),
                        SingleFilter.contains(MunicipalityFields.FederalEntityName.value, it)
                    )
                } ?: EmptyFilters()
            )
        ),
        orders?.let {
            val fields = MunicipalityFields.entries.map { it.value }
            val filteredOrders = orders.mapNotNull { it.takeIf { fields.contains(it["orderBy"]) } }

            Orders.fromValues(filteredOrders.toTypedArray())
        } ?: Orders(
            Order.asc(MunicipalityFields.FederalEntityKeyCode.value),
            Order.asc(MunicipalityFields.KeyCode.value)
        ),
        limit,
        offset
    )
}
