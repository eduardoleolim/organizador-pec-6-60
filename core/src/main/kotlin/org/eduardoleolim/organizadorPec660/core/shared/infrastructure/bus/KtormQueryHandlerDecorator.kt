package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.loadExtensions
import org.ktorm.database.Database

class KtormQueryHandlerDecorator<Q : Query, R : Response>(
    private val database: Database,
    private val queryHandler: QueryHandler<Q, R>,
    private val sqliteExtensions: List<String> = emptyList()
) : QueryHandler<Q, R> {
    override fun handle(query: Q): R {
        database.useConnection {
            it.loadExtensions(sqliteExtensions)
            return queryHandler.handle(query)
        }
    }
}
