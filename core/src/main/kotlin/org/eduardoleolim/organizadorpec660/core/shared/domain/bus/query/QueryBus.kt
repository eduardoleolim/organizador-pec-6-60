package org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query

interface QueryBus {
    @Throws(QueryHandlerExecutionError::class)
    fun <R> ask(query: Query): R
}
