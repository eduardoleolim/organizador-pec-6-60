package org.eduardoleolim.organizadorpec660.role.application

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.role.domain.Role

class RoleResponse(val id: String, val name: String) : Response {
    companion object {
        fun fromAggregate(role: Role) = RoleResponse(role.id().toString(), role.name())
    }
}
