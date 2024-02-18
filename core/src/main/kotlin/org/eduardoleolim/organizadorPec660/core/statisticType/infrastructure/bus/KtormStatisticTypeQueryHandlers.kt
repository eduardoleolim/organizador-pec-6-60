package org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorPec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorPec660.core.statisticType.application.searchById.SearchStatisticTypeByIdQuery
import org.eduardoleolim.organizadorPec660.core.statisticType.application.searchById.SearchStatisticTypeByIdQueryHandler
import org.eduardoleolim.organizadorPec660.core.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.eduardoleolim.organizadorPec660.core.statisticType.application.searchByTerm.SearchStatisticTypesByTermQueryHandler
import org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.persistence.KtormStatisticTypeRepository
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormStatisticTypeQueryHandlers(database: Database) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val statisticTypeRepository: KtormStatisticTypeRepository
    private val searcher: StatisticTypeSearcher

    init {
        statisticTypeRepository = KtormStatisticTypeRepository(database)
        searcher = StatisticTypeSearcher(statisticTypeRepository)

        this[SearchStatisticTypesByTermQuery::class] =
            SearchStatisticTypesByTermQueryHandler(searcher)
        this[SearchStatisticTypeByIdQuery::class] =
            SearchStatisticTypeByIdQueryHandler(searcher)
    }
}
