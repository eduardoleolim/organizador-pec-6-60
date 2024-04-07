package org.eduardoleolim.organizadorpec660.core.statisticType.application.searchById

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query

class SearchStatisticTypeByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
