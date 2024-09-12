package org.eduardoleolim.organizadorpec660.instrument.application.searchById

import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.instrument.application.DetailedInstrumentResponse
import org.eduardoleolim.organizadorpec660.instrument.application.search.InstrumentSearcher
import org.eduardoleolim.organizadorpec660.instrument.domain.*
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeNotFoundError

class SearchInstrumentByIdQueryHandler(
    private val instrumentSearcher: InstrumentSearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val federalEntitySearcher: FederalEntitySearcher,
    private val agencySearcher: AgencySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher
) :
    QueryHandler<SearchInstrumentByIdQuery, DetailedInstrumentResponse> {
    override fun handle(query: SearchInstrumentByIdQuery): DetailedInstrumentResponse {
        val instrumentId = query.id()
        val instrument = searchInstrument(instrumentId) ?: throw InstrumentNotFoundError(instrumentId)

        val fileId = instrument.instrumentFileId().toString()
        val file = searchInstrumentFile(fileId) ?: throw InstrumentFileNotFoundError(fileId)

        val municipalityId = instrument.municipalityId().toString()
        val municipality = searchMunicipality(municipalityId) ?: throw MunicipalityNotFoundError(municipalityId)

        val federalEntityId = municipality.federalEntityId().toString()
        val federalEntity = searhcFederalEntity(federalEntityId) ?: throw FederalEntityNotFoundError(federalEntityId)

        val agencyId = instrument.agencyId().toString()
        val agency = searchAgency(agencyId) ?: throw AgencyNotFoundError(agencyId)

        val statisticTypeId = instrument.statisticTypeId().toString()
        val statisticType = searhcStatisticType(statisticTypeId) ?: throw StatisticTypeNotFoundError(statisticTypeId)

        return DetailedInstrumentResponse.fromAggregate(
            instrument,
            file,
            municipality,
            federalEntity,
            agency,
            statisticType
        )
    }

    private fun searchInstrument(id: String) = InstrumentCriteria.idCriteria(id).let {
        instrumentSearcher.search(it).firstOrNull()
    }

    private fun searchInstrumentFile(instrumentId: String) = instrumentSearcher.searchInstrumentFile(instrumentId)

    private fun searchMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalitySearcher.search(it).firstOrNull()
    }

    private fun searhcFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntitySearcher.search(it).firstOrNull()
    }

    private fun searchAgency(agencyId: String) = AgencyCriteria.idCriteria(agencyId).let {
        agencySearcher.search(it).firstOrNull()
    }

    private fun searhcStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeSearcher.search(it).firstOrNull()
    }
}
