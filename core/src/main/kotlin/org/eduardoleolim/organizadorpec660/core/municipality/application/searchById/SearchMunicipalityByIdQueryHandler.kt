package org.eduardoleolim.organizadorpec660.core.municipality.application.searchById

import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler

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

    private fun searchFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntitySearcher.search(it).first() // If the municipality exists, the federal entity must exists too.
    }
}
