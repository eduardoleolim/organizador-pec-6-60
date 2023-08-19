package org.eduardoleolim.core.federalEntity.application.searchById

import org.eduardoleolim.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.shared.domain.bus.query.QueryHandler

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
