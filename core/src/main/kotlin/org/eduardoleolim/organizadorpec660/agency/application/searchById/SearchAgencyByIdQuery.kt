package org.eduardoleolim.organizadorpec660.agency.application.searchById

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query

class SearchAgencyByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
