/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.auth.application

import org.eduardoleolim.organizadorpec660.role.application.RoleResponse
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.user.domain.User
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
