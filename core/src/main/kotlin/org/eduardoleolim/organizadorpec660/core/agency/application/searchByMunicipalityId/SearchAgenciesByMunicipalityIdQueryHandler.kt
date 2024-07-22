package org.eduardoleolim.organizadorpec660.core.agency.application.searchByMunicipalityId

import org.eduardoleolim.organizadorpec660.core.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.MunicipalityAgenciesResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria

class SearchAgenciesByMunicipalityIdQueryHandler(
    private val agencySearcher: AgencySearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher
) : QueryHandler<SearchAgenciesByMunicipalityIdQuery, MunicipalityAgenciesResponse> {
    private val municipalitiesCache = mutableMapOf<String, Municipality>()
    private val statisticTypesCache = mutableMapOf<String, StatisticType>()

    override fun handle(query: SearchAgenciesByMunicipalityIdQuery): MunicipalityAgenciesResponse {
        val agencies = searchAgencies(query.municipalityId())

        return agencies.map { agency ->
            val municipality = searchMunicipality(agency.municipalityId().toString())

            val statisticTypes = agency.statisticTypeIds().map { statisticTypeId ->
                searchStatisticType(statisticTypeId.value.toString())
            }.sortedBy { it.keyCode() }

            AgencyResponse.fromAggregate(agency, municipality, statisticTypes)
        }.let {
            municipalitiesCache.clear()
            statisticTypesCache.clear()

            MunicipalityAgenciesResponse(it)
        }
    }

    private fun searchAgencies(municipalityId: String) = AgencyCriteria.municipalityIdCriteria(municipalityId).let {
        agencySearcher.search(it)
    }

    private fun searchMunicipality(municipalityId: String): Municipality {
        if (municipalitiesCache.containsKey(municipalityId)) {
            return municipalitiesCache[municipalityId]!!
        }

        val criteria = MunicipalityCriteria.idCriteria(municipalityId)

        return municipalitySearcher.search(criteria).first().also { municipality ->
            municipalitiesCache[municipalityId] = municipality
        }
    }

    private fun searchStatisticType(statisticTypeId: String): StatisticType {
        if (statisticTypesCache.containsKey(statisticTypeId)) {
            return statisticTypesCache[statisticTypeId]!!
        }

        val criteria = StatisticTypeCriteria.idCriteria(statisticTypeId)

        return statisticTypeSearcher.search(criteria).first().also { statisticType ->
            statisticTypesCache[statisticTypeId] = statisticType
        }
    }
}
