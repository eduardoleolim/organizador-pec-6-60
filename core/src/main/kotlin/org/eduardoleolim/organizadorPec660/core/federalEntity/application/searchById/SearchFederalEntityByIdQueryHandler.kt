package org.eduardoleolim.organizadorPec660.core.federalEntity.application.searchById

import org.eduardoleolim.organizadorPec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler

class SearchFederalEntityByIdQueryHandler(private val searcher: FederalEntitySearcher) :
    QueryHandler<SearchFederalEntityByIdQuery, FederalEntityResponse> {
    override fun handle(query: SearchFederalEntityByIdQuery): FederalEntityResponse {
        val federalEntity = searchFederalEntity(query.id()) ?: throw FederalEntityNotFoundError(query.id())

        return FederalEntityResponse.fromAggregate(federalEntity)
    }

    private fun searchFederalEntity(id: String) = FederalEntityCriteria.idCriteria(id).let {
        searcher.search(it).firstOrNull()
    }
}
