package org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.core.municipality.application.searchById.SearchMunicipalityByIdQuery
import org.eduardoleolim.organizadorpec660.core.municipality.application.searchById.SearchMunicipalityByIdQueryHandler
import org.eduardoleolim.organizadorpec660.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormMunicipalityQueryHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>> = mapOf(
        SearchMunicipalitiesByTermQuery::class to searchByTermQueryHandler(),
        SearchMunicipalityByIdQuery::class to searchByIdQueryHandler()
    )

    private fun searchByTermQueryHandler(): QueryHandler<out Query, out Response> {
        val municipalitySearcher: MunicipalitySearcher by inject()
        val federalEntitySearcher: FederalEntitySearcher by inject()
        val queryHandler = SearchMunicipalitiesByTermQueryHandler(municipalitySearcher, federalEntitySearcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByIdQueryHandler(): QueryHandler<out Query, out Response> {
        val municipalitySearcher: MunicipalitySearcher by inject()
        val federalEntitySearcher: FederalEntitySearcher by inject()
        val queryHandler = SearchMunicipalityByIdQueryHandler(municipalitySearcher, federalEntitySearcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
