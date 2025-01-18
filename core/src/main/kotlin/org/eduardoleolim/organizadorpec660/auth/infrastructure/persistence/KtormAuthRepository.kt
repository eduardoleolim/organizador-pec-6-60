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

package org.eduardoleolim.organizadorpec660.auth.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.auth.domain.Auth
import org.eduardoleolim.organizadorpec660.auth.domain.AuthRepository
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.Credentials
import org.ktorm.database.Database
import org.ktorm.dsl.*

class KtormAuthRepository(private val database: Database) : AuthRepository {
    private val credentials = Credentials("c")

    override fun search(emailOrUsername: String): Auth? {
        return database
            .from(credentials)
            .select()
            .where { (credentials.email eq emailOrUsername) or (credentials.username eq emailOrUsername) }
            .map { rowSet ->
                credentials.createEntity(rowSet).let {
                    Auth.from(emailOrUsername, it.password)
                }
            }
            .firstOrNull()
    }
}
