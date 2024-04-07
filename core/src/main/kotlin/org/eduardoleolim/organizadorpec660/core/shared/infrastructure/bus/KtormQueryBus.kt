package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.auth.infrastructure.bus.KtormAuthQueryHandlers
import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.bus.KtormFederalEntityQueryHandlers
import org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.bus.KtormInstrumentTypeQueryHandlers
import org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.bus.KtormMunicipalityQueryHandlers
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.bus.KtormStatisticTypeQueryHandlers
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormQueryBus(database: Database) : QueryBus {
    private val context = KtormAppKoinContext(database)

    private val queryHandlers = HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>().apply {
        putAll(KtormAuthQueryHandlers(context).handlers)
        putAll(KtormFederalEntityQueryHandlers(context).handlers)
        putAll(KtormInstrumentTypeQueryHandlers(context).handlers)
        putAll(KtormMunicipalityQueryHandlers(context).handlers)
        putAll(KtormStatisticTypeQueryHandlers(context).handlers)
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
