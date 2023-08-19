package org.eduardoleolim.core.federalEntity.application.searchById

import org.eduardoleolim.shared.domain.bus.query.Query

class SearchFederalEntityByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
