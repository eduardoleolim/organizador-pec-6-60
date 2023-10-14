package org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchById

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query

class SearchInstrumentTypeByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
