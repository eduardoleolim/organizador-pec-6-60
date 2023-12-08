package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.auth.infrastructure.bus.KtormAuthQueryHandlers
import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.bus.KtormFederalEntityQueryHandlers
import org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.bus.KtormInstrumentTypeQueryHandlers
import org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.bus.KtormMunicipalityQueryHandlers
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.*
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormQueryBus(private val database: Database) : QueryBus {
    private val queryHandlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>>

    init {
        queryHandlers = HashMap()
        queryHandlers.apply {
            putAll(KtormAuthQueryHandlers(database))
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
