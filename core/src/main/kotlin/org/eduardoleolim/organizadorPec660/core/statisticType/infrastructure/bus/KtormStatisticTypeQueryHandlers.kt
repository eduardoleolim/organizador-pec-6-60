package org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorPec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorPec660.core.statisticType.application.searchById.SearchStatisticTypeByIdQuery
import org.eduardoleolim.organizadorPec660.core.statisticType.application.searchById.SearchStatisticTypeByIdQueryHandler
import org.eduardoleolim.organizadorPec660.core.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.eduardoleolim.organizadorPec660.core.statisticType.application.searchByTerm.SearchStatisticTypesByTermQueryHandler
import org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.persistence.KtormStatisticTypeRepository
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormStatisticTypeQueryHandlers(private val database: Database, private val sqliteExtensions: List<String>) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val statisticTypeRepository = KtormStatisticTypeRepository(database)
    private val searcher = StatisticTypeSearcher(statisticTypeRepository)

    init {
        this[SearchStatisticTypesByTermQuery::class] = searchByTermQueryHandler()
        this[SearchStatisticTypeByIdQuery::class] = searchByIdQueryHandler()
    }

    private fun searchByTermQueryHandler(): QueryHandler<out Query, out Response> {
        val queryHandler = SearchStatisticTypesByTermQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler, sqliteExtensions)
    }

    private fun searchByIdQueryHandler(): QueryHandler<out Query, out Response> {
        val queryHandler = SearchStatisticTypeByIdQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler, sqliteExtensions)
    }
}
