package org.eduardoleolim.organizadorpec660.core.instrument.infrastructure.bus

import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.core.instrument.application.search.InstrumentSearcher
import org.eduardoleolim.organizadorpec660.core.instrument.application.searchById.SearchInstrumentByIdQuery
import org.eduardoleolim.organizadorpec660.core.instrument.application.searchById.SearchInstrumentByIdQueryHandler
import org.eduardoleolim.organizadorpec660.core.instrument.application.searchByTerm.SearchInstrumentsByTermQuery
import org.eduardoleolim.organizadorpec660.core.instrument.application.searchByTerm.SearchInstrumentsByTermQueryHandler
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
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

class KtormInstrumentQueryHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>> = mapOf(
        SearchInstrumentByIdQuery::class to searchByIdQueryHandler(),
        SearchInstrumentsByTermQuery::class to searchByTermQueryHandler()
    )

    private fun searchByIdQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val instrumentSearcher: InstrumentSearcher by inject()
        val municipalitySearcher: MunicipalitySearcher by inject()
        val federalEntitySearcher: FederalEntitySearcher by inject()
        val agencySearcher: AgencySearcher by inject()
        val statisticTypeSearcher: StatisticTypeSearcher by inject()
        val queryHandler = SearchInstrumentByIdQueryHandler(
            instrumentSearcher,
            municipalitySearcher,
            federalEntitySearcher,
            agencySearcher,
            statisticTypeSearcher
        )

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByTermQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val instrumentSearcher: InstrumentSearcher by inject()
        val agencySearcher: AgencySearcher by inject()
        val statisticTypeSearcher: StatisticTypeSearcher by inject()
        val federalEntitySearcher: FederalEntitySearcher by inject()
        val municipalitySearcher: MunicipalitySearcher by inject()

        val queryHandler = SearchInstrumentsByTermQueryHandler(
            instrumentSearcher,
            agencySearcher,
            statisticTypeSearcher,
            federalEntitySearcher,
            municipalitySearcher
        )

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
