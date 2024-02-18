package org.eduardoleolim.organizadorPec660.core.instrumentType.application.searchById

import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypeResponse
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.search.InstrumentTypeSearcher
import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentTypeNotFoundError
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler

class SearchInstrumentTypeByIdQueryHandler(private val searcher: InstrumentTypeSearcher) :
    QueryHandler<SearchInstrumentTypeByIdQuery, InstrumentTypeResponse> {
    override fun handle(query: SearchInstrumentTypeByIdQuery): InstrumentTypeResponse {
        val instrumentType = searchInstrumentType(query.id()) ?: throw InstrumentTypeNotFoundError(query.id())

        return InstrumentTypeResponse.fromAggregate(instrumentType)
    }

    private fun searchInstrumentType(id: String) = InstrumentTypeCriteria.idCriteria(id).let {
        searcher.search(it).firstOrNull()
    }
}
