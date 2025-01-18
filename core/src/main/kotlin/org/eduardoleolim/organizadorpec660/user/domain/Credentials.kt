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

class Credentials private constructor(
    private var email: CredentialsEmail,
    private var username: CredentialsUsername,
    private var password: CredentialsPassword
) {
    companion object {
        fun from(email: String, username: String, password: String) = Credentials(
            CredentialsEmail(email),
            CredentialsUsername(username),
            CredentialsPassword(password)
        )
    }

    fun email() = email.value

    fun username() = username.value

    fun password() = password.value

    fun changeEmail(email: String) {
        this.email = CredentialsEmail(email)
    }

    fun changeUsername(username: String) {
        this.username = CredentialsUsername(username)
    }

    fun changePassword(password: String) {
        this.password = CredentialsPassword(password)
    }
}

data class CredentialsEmail(val value: String)

data class CredentialsUsername(val value: String)

data class CredentialsPassword(val value: String)
