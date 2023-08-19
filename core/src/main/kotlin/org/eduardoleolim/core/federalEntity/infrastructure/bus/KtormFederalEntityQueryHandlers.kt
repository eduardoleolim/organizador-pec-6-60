package org.eduardoleolim.core.federalEntity.infrastructure.bus

import org.eduardoleolim.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.core.federalEntity.application.searchById.SearchFederalEntityByIdQuery
import org.eduardoleolim.core.federalEntity.application.searchById.SearchFederalEntityByIdQueryHandler
import org.eduardoleolim.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.shared.domain.bus.query.Query
import org.eduardoleolim.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.shared.domain.bus.query.Response
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormFederalEntityQueryHandlers(database: Database) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val federalEntityRepository: KtormFederalEntityRepository
    private val searcher: FederalEntitySearcher

    init {
        federalEntityRepository = KtormFederalEntityRepository(database)
        searcher = FederalEntitySearcher(federalEntityRepository)

        this[SearchFederalEntityByIdQuery::class] = SearchFederalEntityByIdQueryHandler(searcher)
    }
}