package org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchById

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query

class SearchFederalEntityByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
