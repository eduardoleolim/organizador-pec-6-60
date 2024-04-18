package org.eduardoleolim.organizadorpec660.core.agency.application.searchById

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query

class SearchAgencyByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
