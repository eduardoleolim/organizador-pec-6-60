package org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.searchById.SearchFederalEntityByIdQuery
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.searchById.SearchFederalEntityByIdQueryHandler
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQueryHandler
import org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormFederalEntityQueryHandlers(private val database: Database) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val federalEntityRepository = KtormFederalEntityRepository(database)
    private val searcher = FederalEntitySearcher(federalEntityRepository)

    init {
        this[SearchFederalEntityByIdQuery::class] = searchByIdQueryHandler()
        this[SearchFederalEntitiesByTermQuery::class] = searchByTermQueryHandler()
    }

    private fun searchByIdQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val queryHandler = SearchFederalEntityByIdQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByTermQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val queryHandler = SearchFederalEntitiesByTermQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
