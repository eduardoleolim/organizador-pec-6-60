package org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query

interface QueryHandler<Q : Query, R : Response> {
    fun handle(query: Q): R
}
