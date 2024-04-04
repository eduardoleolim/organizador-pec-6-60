package org.eduardoleolim.organizadorPec660.core.agency.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Orders
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.SingleFilter

enum class AgencyFields(val value: String) {
    Id("id"),
    Name("name"),
    Consecutive("consecutive"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}


object AgencyCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal(AgencyFields.Id.value, id), Orders.none(), 1, null)
}
