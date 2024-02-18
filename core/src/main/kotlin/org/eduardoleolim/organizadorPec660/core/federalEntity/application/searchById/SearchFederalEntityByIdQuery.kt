package org.eduardoleolim.organizadorPec660.core.federalEntity.application.searchById

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query

class SearchFederalEntityByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
