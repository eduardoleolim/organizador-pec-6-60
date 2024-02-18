package org.eduardoleolim.organizadorPec660.core.auth.application

import org.eduardoleolim.organizadorPec660.core.role.application.RoleResponse
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorPec660.core.user.domain.User
import java.util.*

class AuthUserResponse(
    val id: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val username: String,
    val password: String,
    val role: RoleResponse,
    val createdAt: Date,
    val updatedAt: Date?
) : Response {
    companion object {
        fun fromAggregate(user: User) = AuthUserResponse(
            user.id().toString(),
            user.firstName(),
            user.lastName(),
            user.email(),
            user.username(),
            user.password(),
            RoleResponse.fromAggregate(user.role()),
            user.createdAt(),
            user.updatedAt()
        )
    }
}
