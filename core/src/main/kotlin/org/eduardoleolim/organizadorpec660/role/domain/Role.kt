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

package org.eduardoleolim.organizadorpec660.role.domain

import java.util.*

class Role private constructor(
    private val id: RoleId,
    private val name: RoleName
) {
    companion object {
        fun from(id: String, name: String) = Role(
            RoleId.fromString(id),
            RoleName(name)
        )
    }

    fun id() = id.value

    fun name() = name.value
}

data class RoleId(val value: UUID) {
    companion object {
        fun random() = RoleId(UUID.randomUUID())

        fun fromString(value: String) = try {
            RoleId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidRoleIdException(value, e)
        }
    }
}

data class RoleName(val value: String)
