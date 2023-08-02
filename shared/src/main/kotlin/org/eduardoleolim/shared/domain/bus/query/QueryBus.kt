package org.eduardoleolim.shared.domain.bus.query

interface QueryBus {
    @Throws(QueryHandlerExecutionError::class)
    fun <R> ask(query: Query): R
}
