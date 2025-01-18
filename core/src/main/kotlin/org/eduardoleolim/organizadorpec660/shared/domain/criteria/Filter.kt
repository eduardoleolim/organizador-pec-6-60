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

package org.eduardoleolim.organizadorpec660.shared.domain.criteria

import org.eduardoleolim.organizadorpec660.shared.domain.InvalidArgumentError

class Filter(val field: FilterField, val operator: FilterOperator, val value: FilterValue) {
    fun serialize() = String.format("%s.%s.%s", field.value, operator.value, value.value)

    companion object {
        fun create(field: String, operator: String, value: String) =
            Filter(FilterField(field), FilterOperator.fromValue(operator.uppercase()), FilterValue(value))

        fun equal(field: String, value: String) = create(field, "=", value)

        fun notEqual(field: String, value: String) = create(field, "!=", value)

        fun greaterThan(field: String, value: String) = create(field, ">", value)

        fun greaterThanOrEqual(field: String, value: String) = create(field, ">=", value)

        fun lessThan(field: String, value: String) = create(field, "<", value)

        fun lessThanOrEqual(field: String, value: String) = create(field, "<=", value)

        fun contains(field: String, value: String) = create(field, "CONTAINS", value)

        fun notContains(field: String, value: String) = create(field, "NOT_CONTAINS", value)

        fun fromValues(values: HashMap<String, String>): Filter {
            val field = values["field"]
            val operator = values["operator"]
            val value = values["value"]

            if (field == null || operator == null || value == null)
                throw InvalidArgumentError()

            return Filter(FilterField(field), FilterOperator.fromValue(operator), FilterValue(value))
        }
    }
}
