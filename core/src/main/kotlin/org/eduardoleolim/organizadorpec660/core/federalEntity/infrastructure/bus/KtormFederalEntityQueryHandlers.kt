package org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchById.SearchFederalEntityByIdQuery
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchById.SearchFederalEntityByIdQueryHandler
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQueryHandler
import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
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
        this[SearchFederalEntitiesByTermQuery::class] = SearchFederalEntitiesByTermQueryHandler(searcher)
    }
}
