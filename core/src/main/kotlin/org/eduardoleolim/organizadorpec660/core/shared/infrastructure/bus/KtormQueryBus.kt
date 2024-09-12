package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorpec660.agency.infrastructure.bus.KtormAgencyQueryHandlers
import org.eduardoleolim.organizadorpec660.auth.infrastructure.bus.KtormAuthQueryHandlers
import org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.bus.KtormFederalEntityQueryHandlers
import org.eduardoleolim.organizadorpec660.core.instrument.infrastructure.bus.KtormInstrumentQueryHandlers
import org.eduardoleolim.organizadorpec660.municipality.infrastructure.bus.KtormMunicipalityQueryHandlers
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.statisticType.infrastructure.bus.KtormStatisticTypeQueryHandlers
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormQueryBus(database: Database, instrumentsPath: String) : QueryBus {
    private val context = KtormAppKoinContext(database, instrumentsPath)

    private val queryHandlers = HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>().apply {
        putAll(KtormAgencyQueryHandlers(context).handlers)
        putAll(KtormAuthQueryHandlers(context).handlers)
        putAll(KtormFederalEntityQueryHandlers(context).handlers)
        putAll(KtormInstrumentQueryHandlers(context).handlers)
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
