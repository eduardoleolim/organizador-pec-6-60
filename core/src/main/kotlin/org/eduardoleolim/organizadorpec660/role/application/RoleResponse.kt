package org.eduardoleolim.organizadorpec660.role.application

import org.eduardoleolim.organizadorpec660.role.domain.Role
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response

class RoleResponse(val id: String, val name: String) : Response {
    companion object {
        fun fromAggregate(role: Role) = RoleResponse(role.id().toString(), role.name())
    }
}
