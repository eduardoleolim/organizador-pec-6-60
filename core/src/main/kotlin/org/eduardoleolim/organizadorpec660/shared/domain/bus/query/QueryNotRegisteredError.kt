package org.eduardoleolim.organizadorpec660.shared.domain.bus.query

import kotlin.reflect.KClass

class QueryNotRegisteredError(query: KClass<out Query>) :
    Exception("The query ${query.simpleName} hasn't a query handler associated")
