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
}

open class MultipleFilters(val filters: List<Filters>, val operator: FiltersOperator) : Filters() {
    override fun isEmpty() = filters.isEmpty()

    override val isMultiple = true

    override fun serialize(): String {
        return filters.joinToString(separator = "^") { it.serialize() }
    }
}

class AndFilters(filters: List<Filters>) : MultipleFilters(filters, FiltersOperator.AND)

class OrFilters(filters: List<Filters>) : MultipleFilters(filters, FiltersOperator.OR)
