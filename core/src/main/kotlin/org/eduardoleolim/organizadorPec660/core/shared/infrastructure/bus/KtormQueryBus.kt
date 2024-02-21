package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.auth.infrastructure.bus.KtormAuthQueryHandlers
import org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.bus.KtormFederalEntityQueryHandlers
import org.eduardoleolim.organizadorPec660.core.instrumentType.infrastructure.bus.KtormInstrumentTypeQueryHandlers
import org.eduardoleolim.organizadorPec660.core.municipality.infrastructure.bus.KtormMunicipalityQueryHandlers
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.*
import org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.bus.KtormStatisticTypeQueryHandlers
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormQueryBus(private val database: Database, sqliteExtensions: List<String> = emptyList()) : QueryBus {
    private val queryHandlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>> =
        HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>().apply {
            putAll(KtormAuthQueryHandlers(database))
            putAll(KtormFederalEntityQueryHandlers(database, sqliteExtensions))
            putAll(KtormInstrumentTypeQueryHandlers(database, sqliteExtensions))
            putAll(KtormMunicipalityQueryHandlers(database, sqliteExtensions))
            putAll(KtormStatisticTypeQueryHandlers(database, sqliteExtensions))
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
