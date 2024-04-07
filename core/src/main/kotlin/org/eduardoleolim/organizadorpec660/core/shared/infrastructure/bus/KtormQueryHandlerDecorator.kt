package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.ktorm.database.Database

class KtormQueryHandlerDecorator<Q : Query, R : Response>(
    private val database: Database,
    private val queryHandler: QueryHandler<Q, R>
) : QueryHandler<Q, R> {
    override fun handle(query: Q): R {
        database.useConnection {
            return queryHandler.handle(query)
        }
    }
}
