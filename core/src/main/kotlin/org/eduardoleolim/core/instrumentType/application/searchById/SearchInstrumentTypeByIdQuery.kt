package org.eduardoleolim.core.instrumentType.application.searchById

import org.eduardoleolim.shared.domain.bus.query.Query

class SearchInstrumentTypeByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
