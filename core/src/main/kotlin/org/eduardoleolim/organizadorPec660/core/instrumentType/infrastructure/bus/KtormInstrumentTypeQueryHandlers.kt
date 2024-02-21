package org.eduardoleolim.organizadorPec660.core.instrumentType.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.instrumentType.application.search.InstrumentTypeSearcher
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.searchById.SearchInstrumentTypeByIdQuery
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.searchById.SearchInstrumentTypeByIdQueryHandler
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQuery
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQueryHandler
import org.eduardoleolim.organizadorPec660.core.instrumentType.infrastructure.persistence.KtormInstrumentTypeRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormInstrumentTypeQueryHandlers(private val database: Database, private val sqliteExtensions: List<String>) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val instrumentTypeRepository = KtormInstrumentTypeRepository(database)
    private val instrumentTypeSearcher = InstrumentTypeSearcher(instrumentTypeRepository)

    init {
        this[SearchInstrumentTypeByIdQuery::class] = searchByIdQueryHandler()
        this[SearchInstrumentTypesByTermQuery::class] = searchByTermQueryHandler()
    }

    private fun searchByIdQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val queryHandler = SearchInstrumentTypeByIdQueryHandler(instrumentTypeSearcher)

        return KtormQueryHandlerDecorator(database, queryHandler, sqliteExtensions)
    }

    private fun searchByTermQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val queryHandler = SearchInstrumentTypesByTermQueryHandler(instrumentTypeSearcher)

        return KtormQueryHandlerDecorator(database, queryHandler, sqliteExtensions)
    }
}
