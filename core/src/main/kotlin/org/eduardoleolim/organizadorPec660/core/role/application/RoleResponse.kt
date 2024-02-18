package org.eduardoleolim.organizadorPec660.core.role.application

import org.eduardoleolim.organizadorPec660.core.role.domain.Role
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response

class RoleResponse(val id: String, val name: String) : Response {
    companion object {
        fun fromAggregate(role: Role) = RoleResponse(role.id().toString(), role.name())
    }
}
