package org.eduardoleolim.core.shared.infrastructure.bus

import org.eduardoleolim.core.federalEntity.infrastructure.bus.KtormFederalEntityQueryHandlers
import org.eduardoleolim.core.instrumentType.infrastructure.bus.KtormInstrumentTypeQueryHandlers
import org.eduardoleolim.core.municipality.infrastructure.bus.KtormMunicipalityQueryHandlers
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.*
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormQueryBus(private val database: Database) : QueryBus {
    private val queryHandlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>>

    init {
        queryHandlers = HashMap()
        queryHandlers.apply {
            putAll(KtormFederalEntityQueryHandlers(database))
            putAll(KtormInstrumentTypeQueryHandlers(database))
            putAll(KtormMunicipalityQueryHandlers(database))
        }
    }

    override fun <R> ask(query: Query): R {
        try {
            return queryHandlers[query::class]?.let {
                @Suppress("UNCHECKED_CAST")
                it as QueryHandler<Query, Response>
                @Suppress("UNCHECKED_CAST")
                it.handle(query) as R
            } ?: throw QueryNotRegisteredError(query::class)
        } catch (e: Exception) {
            throw QueryHandlerExecutionError(e)
        }
    }

}
