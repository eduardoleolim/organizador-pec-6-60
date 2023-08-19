package org.eduardoleolim.core.federalEntity.application.searchByTerm

import org.eduardoleolim.shared.domain.bus.query.Query

class SearchFederalEntitiesByTermQuery(
    search: String? = null,
    private val orders: Array<HashMap<String, String>>? = null,
    private val limit: Int? = null,
    private val offset: Int? = null
) : Query {
    private val search = search?.trim()

    fun search() = search

    fun orders() = orders

    fun limit() = limit

    fun offset() = offset
}
