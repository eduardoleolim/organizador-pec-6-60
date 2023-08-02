package org.eduardoleolim.shared.domain.bus.query

class QueryNotRegisteredError(query: Class<out Query>) :
    Exception(String.format("The query <%s> hasn't a query handler associated", query.toString()))
