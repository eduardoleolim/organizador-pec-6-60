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

enum class FiltersOperator {
    AND,
    OR
}

sealed class Filters {
    abstract fun serialize(): String

    abstract fun isEmpty(): Boolean

    abstract val isMultiple: Boolean
}

class EmptyFilters : Filters() {
    override fun isEmpty() = true

    override val isMultiple = false

    override fun serialize(): String {
        return ""
    }
}

class SingleFilter(val filter: Filter) : Filters() {
    override fun isEmpty() = false

    override val isMultiple = false

    override fun serialize(): String {
        return filter.serialize()
    }

    companion object {
        fun equal(field: String, value: String) = SingleFilter(Filter.equal(field, value))

        fun notEqual(field: String, value: String) = SingleFilter(Filter.notEqual(field, value))

        fun greaterThan(field: String, value: String) = SingleFilter(Filter.greaterThan(field, value))

        fun greaterThanOrEqual(field: String, value: String) = SingleFilter(Filter.greaterThanOrEqual(field, value))

        fun lessThan(field: String, value: String) = SingleFilter(Filter.lessThan(field, value))

        fun lessThanOrEqual(field: String, value: String) = SingleFilter(Filter.lessThanOrEqual(field, value))

        fun contains(field: String, value: String) = SingleFilter(Filter.contains(field, value))

        fun notContains(field: String, value: String) = SingleFilter(Filter.notContains(field, value))
    }
}

open class MultipleFilters(val filters: List<Filters>, val operator: FiltersOperator) : Filters() {
    constructor(vararg filters: Filters, operator: FiltersOperator) : this(filters.toList(), operator)

    override fun isEmpty() = filters.isEmpty()

    override val isMultiple = true

    override fun serialize(): String {
        return filters.joinToString(separator = "^") { it.serialize() }
    }
}

class AndFilters(filters: List<Filters>) : MultipleFilters(filters, FiltersOperator.AND) {
    constructor(vararg filters: Filters) : this(filters.toList())
}

class OrFilters(filters: List<Filters>) : MultipleFilters(filters, FiltersOperator.OR) {
    constructor(vararg filters: Filters) : this(filters.toList())
}
