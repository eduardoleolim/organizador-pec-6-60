package org.eduardoleolim.shared.domain.criteria

class Filters(val filters: List<Filter>) {
    fun serialize() = filters.joinToString(separator = "^", transform = Filter::serialize)

    companion object {
        fun none() = Filters(emptyList())

        fun fromValues(filters: Array<HashMap<String, String>>) = Filters(filters.map { Filter.fromValues(it) })
    }
}
