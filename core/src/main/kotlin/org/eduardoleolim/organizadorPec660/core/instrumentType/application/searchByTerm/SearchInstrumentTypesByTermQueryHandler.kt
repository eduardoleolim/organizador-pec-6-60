package org.eduardoleolim.organizadorPec660.core.instrumentType.application.searchByTerm

import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypesResponse
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.search.InstrumentTypeSearcher
import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler

class SearchInstrumentTypesByTermQueryHandler(private val searcher: InstrumentTypeSearcher) :
    QueryHandler<SearchInstrumentTypesByTermQuery, InstrumentTypesResponse> {
    override fun handle(query: SearchInstrumentTypesByTermQuery): InstrumentTypesResponse {
        val instrumentTypes = searchInstrumentTypes(query.search(), query.orders(), query.limit(), query.offset())
        val totalInstrumentTypes = countTotalInstrumentTypes(query.search())

        return InstrumentTypesResponse.fromAggregate(
            instrumentTypes,
            totalInstrumentTypes,
            query.limit(),
            query.offset()
        )
    }

    private fun searchInstrumentTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = InstrumentTypeCriteria.searchCriteria(
        search = search,
        orders = orders,
        limit = limit,
        offset = offset
    ).let {
        searcher.search(it)
    }

    private fun countTotalInstrumentTypes(search: String? = null) = InstrumentTypeCriteria.searchCriteria(
        search = search
    ).let {
        searcher.count(it)
    }
}
