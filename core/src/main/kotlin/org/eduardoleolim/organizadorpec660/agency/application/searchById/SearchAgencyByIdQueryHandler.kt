package org.eduardoleolim.organizadorpec660.agency.application.searchById

import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorpec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria

class SearchAgencyByIdQueryHandler(
    private val agencySearcher: AgencySearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher
) : QueryHandler<SearchAgencyByIdQuery, AgencyResponse> {
    override fun handle(query: SearchAgencyByIdQuery): AgencyResponse {
        val agency = searchAgency(query.id()) ?: throw AgencyNotFoundError(query.id())

        val municipalities = searchMunicipality(agency.municipalityId().toString())

        val statisticTypesAndInstrumentTypes = agency.statisticTypeIds().map { statisticTypeId ->
            searchStatisticType(statisticTypeId.value.toString())
        }

        return AgencyResponse.fromAggregate(agency, municipalities, statisticTypesAndInstrumentTypes)
    }

    private fun searchAgency(id: String) = AgencyCriteria.idCriteria(id).let {
        agencySearcher.search(it).firstOrNull()
    }

    private fun searchMunicipality(municipalityId: String): Municipality {
        val criteria = MunicipalityCriteria.idCriteria(municipalityId)

        return municipalitySearcher.search(criteria).first()
    }

    private fun searchStatisticType(statisticTypeId: String): StatisticType {
        val criteria = StatisticTypeCriteria.idCriteria(statisticTypeId)

        return statisticTypeSearcher.search(criteria).first()
    }
}
