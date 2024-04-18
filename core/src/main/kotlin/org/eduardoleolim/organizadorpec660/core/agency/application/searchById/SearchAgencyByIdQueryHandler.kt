package org.eduardoleolim.organizadorpec660.core.agency.application.searchById

import org.eduardoleolim.organizadorpec660.core.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.core.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyNotFoundError
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

class SearchAgencyByIdQueryHandler(
    private val agencySearcher: AgencySearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher,
    private val instrumentTypeSearcher: InstrumentTypeSearcher
) : QueryHandler<SearchAgencyByIdQuery, AgencyResponse> {
    private val municipalitiesCache = mutableMapOf<String, Municipality>()
    private val statisticTypesCache = mutableMapOf<String, StatisticType>()
    private val instrumentTypesCache = mutableMapOf<String, InstrumentType>()

    override fun handle(query: SearchAgencyByIdQuery): AgencyResponse {
        val agency = searchAgency(query.id()) ?: throw AgencyNotFoundError(query.id())

        val municipalities = agency.municipalities().map { municipalityAssociation ->
            val municipality = searchMunicipality(municipalityAssociation.municipalityId().toString())

            Pair(municipality, municipalityAssociation.isOwner())
        }

        val statisticTypesAndInstrumentTypes = agency.statisticTypes().map { statisticTypeAssociation ->
            val statisticType = searchStatisticType(statisticTypeAssociation.statisticTypeId().toString())
            val instrumentType = searchInstrumentType(statisticTypeAssociation.instrumentTypeId().toString())

            Pair(statisticType, instrumentType)
        }

        return AgencyResponse.fromAggregate(agency, municipalities, statisticTypesAndInstrumentTypes)
    }

    private fun searchAgency(id: String) = AgencyCriteria.idCriteria(id).let {
        agencySearcher.search(it).firstOrNull()
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
