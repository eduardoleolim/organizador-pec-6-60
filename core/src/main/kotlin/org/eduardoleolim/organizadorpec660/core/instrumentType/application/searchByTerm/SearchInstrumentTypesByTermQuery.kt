package org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchByTerm

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query


class SearchInstrumentTypesByTermQuery(
    search: String? = null,
    private val orders: Array<HashMap<String, String>>? = null,
    private val limit: Int? = null,
    private val offset: Int? = null
) : Query {
    private val search = search?.trim()

    fun search(): String? {
        return search
    }

    fun orders(): Array<HashMap<String, String>>? {
        return orders
    }

    fun limit(): Int? {
        return limit
    }

    fun offset(): Int? {
        return offset
    }
}