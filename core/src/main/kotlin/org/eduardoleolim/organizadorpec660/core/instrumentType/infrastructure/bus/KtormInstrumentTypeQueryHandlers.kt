package org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.instrumentType.application.search.InstrumentTypeSearcher
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchById.SearchInstrumentTypeByIdQuery
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchById.SearchInstrumentTypeByIdQueryHandler
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQuery
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormInstrumentTypeQueryHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>> = mapOf(
        SearchInstrumentTypeByIdQuery::class to searchByIdQueryHandler(),
        SearchInstrumentTypesByTermQuery::class to searchByTermQueryHandler()
    )

    private fun searchByIdQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val searcher: InstrumentTypeSearcher by inject()
        val queryHandler = SearchInstrumentTypeByIdQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByTermQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val searcher: InstrumentTypeSearcher by inject()
        val queryHandler = SearchInstrumentTypesByTermQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
