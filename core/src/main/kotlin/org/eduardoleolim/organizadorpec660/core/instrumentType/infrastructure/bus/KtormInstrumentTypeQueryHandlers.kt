package org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.instrumentType.application.search.InstrumentTypeSearcher
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchById.SearchInstrumentTypeByIdQuery
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchById.SearchInstrumentTypeByIdQueryHandler
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQuery
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQueryHandler
import org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.persistence.KtormInstrumentTypeRepository
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormInstrumentTypeQueryHandlers(database: Database) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val instrumentTypeRepository: KtormInstrumentTypeRepository
    private val instrumentTypeSearcher: InstrumentTypeSearcher

    init {
        instrumentTypeRepository = KtormInstrumentTypeRepository(database)
        instrumentTypeSearcher = InstrumentTypeSearcher(instrumentTypeRepository)

        this[SearchInstrumentTypeByIdQuery::class] = SearchInstrumentTypeByIdQueryHandler(instrumentTypeSearcher)
        this[SearchInstrumentTypesByTermQuery::class] = SearchInstrumentTypesByTermQueryHandler(instrumentTypeSearcher)
    }
}
