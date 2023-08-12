package org.eduardoleolim.core.shared.infrastructure.bus

import org.eduardoleolim.shared.domain.bus.query.*
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormQueryBus(private val database: Database) : QueryBus {
    private val queries: HashMap<KClass<Query>, QueryHandler<Query, Response>> = HashMap()

    override fun <R> ask(query: Query): R {
        try {
            return queries[query::class]?.let {
                @Suppress("UNCHECKED_CAST")
                it.handle(query) as R
            } ?: throw QueryNotRegisteredError(query::class)
        } catch (e: Exception) {
            throw QueryHandlerExecutionError(e)
        }
    }

}
