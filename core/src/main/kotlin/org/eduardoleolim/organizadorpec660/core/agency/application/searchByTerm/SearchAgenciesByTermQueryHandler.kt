package org.eduardoleolim.organizadorpec660.core.agency.application.searchByTerm

import org.eduardoleolim.organizadorpec660.core.agency.application.AgenciesResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.search.InstrumentTypeSearcher
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria

class SearchAgenciesByTermQueryHandler(
    private val agencySearcher: AgencySearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher,
    private val instrumentTypeSearcher: InstrumentTypeSearcher
) : QueryHandler<SearchAgenciesByTermQuery, AgenciesResponse> {
    private val municipalitiesCache = mutableMapOf<String, Municipality>()
    private val statisticTypesCache = mutableMapOf<String, StatisticType>()
    private val instrumentTypesCache = mutableMapOf<String, InstrumentType>()

    override fun handle(query: SearchAgenciesByTermQuery): AgenciesResponse {
        val agencies = searchAgencies(query.search(), query.orders(), query.limit(), query.offset())
        val totalAgencies = countTotalAgencies(query.search())

        return agencies.map { agency ->
            val municipalities = agency.municipalities().map { municipalityAssociation ->
                val municipality = searchMunicipality(municipalityAssociation.municipalityId().toString())

                Pair(municipality, municipalityAssociation.isOwner())
            }

            val statisticTypesAndInstrumentTypes = agency.statisticTypes().map { statisticTypeAssociation ->
                val statisticType = searchStatisticType(statisticTypeAssociation.statisticTypeId().toString())
                val instrumentType = searchInstrumentType(statisticTypeAssociation.instrumentTypeId().toString())

                Pair(statisticType, instrumentType)
            }

            AgencyResponse.fromAggregate(agency, municipalities, statisticTypesAndInstrumentTypes)
        }.let {
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

    private fun countTotalAgencies(search: String? = null) = AgencyCriteria.searchCriteria(
        search = search
    ).let {
        agencySearcher.count(it)
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

    private fun searchInstrumentType(instrumentTypeId: String): InstrumentType {
        if (instrumentTypesCache.containsKey(instrumentTypeId)) {
            return instrumentTypesCache[instrumentTypeId]!!
        }

        val criteria = InstrumentTypeCriteria.idCriteria(instrumentTypeId)

        return instrumentTypeSearcher.search(criteria).first().also { instrumentType ->
            instrumentTypesCache[instrumentTypeId] = instrumentType
        }
    }
}
