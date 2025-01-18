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

package org.eduardoleolim.organizadorpec660.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface Credential : Entity<Credential> {
    val email: String
    val username: String
    val password: String
    val user: User
}

class Credentials(alias: String? = null) : Table<Credential>("credentials", alias) {
    val email = varchar("email").bindTo { it.email }
    val username = varchar("username").bindTo { it.username }
    val password = varchar("password").bindTo { it.password }
    val userId = varchar("userId").primaryKey().references(Users()) { it.user }

    override fun aliased(alias: String) = Credentials(alias)
}
