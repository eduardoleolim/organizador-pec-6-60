package org.eduardoleolim.organizadorPec660.core.federalEntity.application.searchByTerm

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query

class SearchFederalEntitiesByTermQuery(
    search: String? = null,
    private val orders: Array<HashMap<String, String>>? = null,
    private val limit: Int? = null,
    private val offset: Int? = null
) : Query {
    private val search = search?.trim()?.uppercase()

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
