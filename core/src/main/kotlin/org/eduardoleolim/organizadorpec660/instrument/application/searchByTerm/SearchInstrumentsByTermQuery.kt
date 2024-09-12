package org.eduardoleolim.organizadorpec660.instrument.application.searchByTerm

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query

class SearchInstrumentsByTermQuery(
    federalEntityId: String? = null,
    municipalityId: String? = null,
    agencyId: String? = null,
    statisticTypeId: String? = null,
    private val year: Int? = null,
    private val month: Int? = null,
    search: String? = null,
    private val orders: Array<HashMap<String, String>>? = null,
    private val limit: Int? = null,
    private val offset: Int? = null
) : Query {
    private val federalEntityId = federalEntityId?.trim()
    private val municipalityId = municipalityId?.trim()
    private val agencyId = agencyId?.trim()
    private val statisticTypeId = statisticTypeId?.trim()
    private val search = search?.trim()?.uppercase()

    fun federalEntityId(): String? {
        return federalEntityId
    }

    fun municipalityId(): String? {
        return municipalityId
    }

    fun agencyId(): String? {
        return agencyId
    }

    fun statisticTypeId(): String? {
        return statisticTypeId
    }

    fun year(): Int? {
        return year
    }

    fun month(): Int? {
        return month
    }

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
