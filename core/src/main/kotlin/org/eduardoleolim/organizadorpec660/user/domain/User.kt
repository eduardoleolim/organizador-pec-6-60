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

package org.eduardoleolim.organizadorpec660.user.domain

import org.eduardoleolim.organizadorpec660.role.domain.Role
import java.util.*

class User private constructor(
    private val id: UserId,
    private var firstName: UserFirstName,
    private var lastName: UserLastName,
    private val credentials: Credentials,
    private var role: Role,
    private val createdAt: UserCreateDate,
    private var updatedAt: UserUpdateDate?
) {
    companion object {
        fun create(firstName: String, lastName: String, email: String, username: String, password: String, role: Role) =
            User(
                UserId.random(),
                UserFirstName(firstName),
                UserLastName(lastName),
                Credentials.from(email, username, password),
                role,
                UserCreateDate.now(),
                null
            )

        fun from(
            id: String,
            firstName: String,
            lastName: String,
            email: String,
            username: String,
            password: String,
            role: Role,
            createdAt: Date,
            updatedAt: Date?
        ) = User(
            UserId.fromString(id),
            UserFirstName(firstName),
            UserLastName(lastName),
            Credentials.from(email, username, password),
            role,
            UserCreateDate(createdAt),
            updatedAt?.let {
                if (it.before(createdAt)) throw InvalidUserUpdateDateError(it, createdAt)
                UserUpdateDate(it)
            }
        )
    }

    fun id() = id.value

    fun firstName() = firstName.value

    fun lastName() = lastName.value

    fun role() = role

    fun createdAt() = createdAt.value

    fun updatedAt() = updatedAt?.value

    fun email() = credentials.email()

    fun username() = credentials.username()

    fun password() = credentials.password()

    fun changeFirstName(firstName: String) {
        this.firstName = UserFirstName(firstName)
        this.updatedAt = UserUpdateDate.now()
    }

    fun changeLastName(lastName: String) {
        this.lastName = UserLastName(lastName)
        this.updatedAt = UserUpdateDate.now()
    }

    fun changeRole(role: Role) {
        this.role = role
        this.updatedAt = UserUpdateDate.now()
    }

    fun changeEmail(email: String) {
        credentials.changeEmail(email)
        this.updatedAt = UserUpdateDate.now()
    }

    fun changeUsername(username: String) {
        credentials.changeUsername(username)
        this.updatedAt = UserUpdateDate.now()
    }

    fun changePassword(password: String) {
        credentials.changePassword(password)
        this.updatedAt = UserUpdateDate.now()
    }
}

data class UserId(val value: UUID) {
    companion object {
        fun random() = UserId(UUID.randomUUID())

        fun fromString(value: String) = try {
            UserId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidUserIdError(value, e)
        }
    }
}

data class UserFirstName(val value: String)

data class UserLastName(val value: String)

data class UserCreateDate(val value: Date) {
    companion object {
        fun now() = UserCreateDate(Date())
    }
}

data class UserUpdateDate(val value: Date) {
    companion object {
        fun now() = UserUpdateDate(Date())
    }
}
