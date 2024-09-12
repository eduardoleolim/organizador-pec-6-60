package org.eduardoleolim.organizadorpec660.agency.application.searchByTerm

import org.eduardoleolim.organizadorpec660.agency.application.AgenciesResponse
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria

class SearchAgenciesByTermQueryHandler(
    private val agencySearcher: AgencySearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher
) : QueryHandler<SearchAgenciesByTermQuery, AgenciesResponse> {
    private val municipalitiesCache = mutableMapOf<String, Municipality>()
    private val statisticTypesCache = mutableMapOf<String, StatisticType>()

    override fun handle(query: SearchAgenciesByTermQuery): AgenciesResponse {
        val agencies = searchAgencies(query.search(), query.orders(), query.limit(), query.offset())
        val totalAgencies = countTotalAgencies(query.search())

        return agencies.map { agency ->
            val municipalityId = agency.municipalityId().toString()
            val municipality = municipalitiesCache[municipalityId] ?: searchMunicipality(municipalityId).also {
                municipalitiesCache[municipalityId] = it
            }

            val statisticTypes = agency.statisticTypeIds().map { statisticTypeId ->
                val id = statisticTypeId.value.toString()

                statisticTypesCache[id] ?: searchStatisticType(id).also { statisticTypesCache[id] = it }
            }.sortedBy { it.keyCode() }

            AgencyResponse.fromAggregate(agency, municipality, statisticTypes)
        }.let {
            municipalitiesCache.clear()
            statisticTypesCache.clear()

            AgenciesResponse(it, totalAgencies, query.limit(), query.offset())
        }
    }

    private fun searchAgencies(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = AgencyCriteria.searchCriteria(
        search = search,
        orders = orders,
        limit = limit,
        offset = offset
    ).let {
        agencySearcher.search(it)
    }

    private fun countTotalAgencies(search: String? = null) = AgencyCriteria.searchCriteria(search).let {
        agencySearcher.count(it)
    }

    private fun searchMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalitySearcher.search(it).first()
    }

    private fun searchStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeSearcher.search(it).first()
    }
}
