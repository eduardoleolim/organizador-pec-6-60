package org.eduardoleolim.core.instrumentType.application.searchByTerm

import org.eduardoleolim.core.instrumentType.application.InstrumentTypesResponse
import org.eduardoleolim.core.instrumentType.application.search.InstrumentTypeSearcher
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.shared.domain.criteria.Order

class SearchInstrumentTypeByTermQueryHandler(private val searcher: InstrumentTypeSearcher) :
    QueryHandler<SearchInstrumentTypeByTermQuery, InstrumentTypesResponse> {
    override fun handle(query: SearchInstrumentTypeByTermQuery): InstrumentTypesResponse {
        val instrumentTypes = searchInstrumentTypes(query.search(), query.orders(), query.limit(), query.offset())

        return InstrumentTypesResponse.fromAggregate(instrumentTypes)
    }

    private fun searchInstrumentTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = InstrumentTypeCriteria.searchCriteria(
        search = search,
        orders = orders?.map { Order.fromValues(it["orderBy"], it["orderType"]) }?.toMutableList(),
        limit = limit,
        offset = offset
    ).let {
        searcher.search(it)
    }
}
