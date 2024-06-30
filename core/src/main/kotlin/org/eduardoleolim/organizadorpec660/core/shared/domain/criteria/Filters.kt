package org.eduardoleolim.organizadorpec660.core.shared.domain.criteria

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
