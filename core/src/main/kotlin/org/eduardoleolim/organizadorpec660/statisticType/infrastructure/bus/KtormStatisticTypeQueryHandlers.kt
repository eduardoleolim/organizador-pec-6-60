package org.eduardoleolim.organizadorpec660.statisticType.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.application.searchById.SearchStatisticTypeByIdQuery
import org.eduardoleolim.organizadorpec660.statisticType.application.searchById.SearchStatisticTypeByIdQueryHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.eduardoleolim.organizadorpec660.statisticType.application.searchByTerm.SearchStatisticTypesByTermQueryHandler
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormStatisticTypeQueryHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>> = mapOf(
        SearchStatisticTypesByTermQuery::class to searchByTermQueryHandler(),
        SearchStatisticTypeByIdQuery::class to searchByIdQueryHandler()
    )

    private fun searchByTermQueryHandler(): QueryHandler<out Query, out Response> {
        val searcher: StatisticTypeSearcher by inject()
        val queryHandler = SearchStatisticTypesByTermQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByIdQueryHandler(): QueryHandler<out Query, out Response> {
        val searcher: StatisticTypeSearcher by inject()
        val queryHandler = SearchStatisticTypeByIdQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
