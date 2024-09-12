package org.eduardoleolim.organizadorpec660.instrument.application.searchById

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query

class SearchInstrumentByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
