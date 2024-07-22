package org.eduardoleolim.organizadorpec660.core.agency.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.core.agency.application.searchById.SearchAgencyByIdQuery
import org.eduardoleolim.organizadorpec660.core.agency.application.searchById.SearchAgencyByIdQueryHandler
import org.eduardoleolim.organizadorpec660.core.agency.application.searchByMunicipalityId.SearchAgenciesByMunicipalityIdQuery
import org.eduardoleolim.organizadorpec660.core.agency.application.searchByMunicipalityId.SearchAgenciesByMunicipalityIdQueryHandler
import org.eduardoleolim.organizadorpec660.core.agency.application.searchByTerm.SearchAgenciesByTermQuery
import org.eduardoleolim.organizadorpec660.core.agency.application.searchByTerm.SearchAgenciesByTermQueryHandler
import org.eduardoleolim.organizadorpec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.core.statisticType.application.search.StatisticTypeSearcher
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormAgencyQueryHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>> = mapOf(
        SearchAgencyByIdQuery::class to searchByIdQueryHandler(),
        SearchAgenciesByTermQuery::class to searchByTermQueryHandler(),
        SearchAgenciesByMunicipalityIdQuery::class to searchByMunicipalityIdQueryHandler()
    )

    private fun searchByIdQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val agencySearcher: AgencySearcher by inject()
        val municipalitySearcher: MunicipalitySearcher by inject()
        val statisticTypeSearcher: StatisticTypeSearcher by inject()
        val queryHandler = SearchAgencyByIdQueryHandler(
            agencySearcher,
            municipalitySearcher,
            statisticTypeSearcher
        )

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByTermQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val agencySearcher: AgencySearcher by inject()
        val municipalitySearcher: MunicipalitySearcher by inject()
        val statisticTypeSearcher: StatisticTypeSearcher by inject()
        val queryHandler = SearchAgenciesByTermQueryHandler(
            agencySearcher,
            municipalitySearcher,
            statisticTypeSearcher
        )

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByMunicipalityIdQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val agencySearcher: AgencySearcher by inject()
        val municipalitySearcher: MunicipalitySearcher by inject()
        val statisticTypeSearcher: StatisticTypeSearcher by inject()
        val queryHandler = SearchAgenciesByMunicipalityIdQueryHandler(
            agencySearcher,
            municipalitySearcher,
            statisticTypeSearcher
        )

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
