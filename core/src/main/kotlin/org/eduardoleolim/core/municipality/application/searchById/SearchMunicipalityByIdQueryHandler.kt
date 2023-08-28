package org.eduardoleolim.core.municipality.application.searchById

import org.eduardoleolim.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.shared.domain.bus.query.QueryHandler

class SearchMunicipalityByIdQueryHandler(
    private val municipalitySearcher: MunicipalitySearcher,
    private val federalEntitySearcher: FederalEntitySearcher
) : QueryHandler<SearchMunicipalityByIdQuery, MunicipalityResponse> {
    override fun handle(query: SearchMunicipalityByIdQuery): MunicipalityResponse {
        val municipality = searchMunicipality(query.id()) ?: throw MunicipalityNotFoundError(query.id())
        val federalEntity = searchFederalEntity(municipality.federalEntityId().toString())

        return MunicipalityResponse.fromAggregate(municipality, federalEntity)
    }

    private fun searchMunicipality(id: String) = MunicipalityCriteria.idCriteria(id).let {
        municipalitySearcher.search(it).firstOrNull()
    }

    private fun searchFederalEntity(id: String) = FederalEntityCriteria.idCriteria(id).let {
        federalEntitySearcher.search(it).first() // If the municipality exists, the federal entity must exist too.
    }
}
