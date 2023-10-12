package org.eduardoleolim.organizadorpec660.shared.domain.bus.query

interface QueryHandler<Q : Query, R : Response> {
    fun handle(query: Q): R
}
