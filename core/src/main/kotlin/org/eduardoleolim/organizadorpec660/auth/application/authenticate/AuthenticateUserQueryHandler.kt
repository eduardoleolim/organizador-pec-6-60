/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.organizadorpec660.auth.application.authenticate

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.auth.domain.AuthError
import org.eduardoleolim.organizadorpec660.auth.domain.InvalidAuthCredentialsError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.user.application.search.UserSearcher
import org.eduardoleolim.organizadorpec660.user.domain.UserCriteria

class AuthenticateUserQueryHandler(private val authenticator: UserAuthenticator, private val searcher: UserSearcher) :
    QueryHandler<AuthError, AuthUserResponse, AuthenticateUserQuery> {
    override fun handle(query: AuthenticateUserQuery): Either<AuthError, AuthUserResponse> {
        return authenticator.authenticate(query.emailOrUsername(), query.password()).let { authenticated ->
            if (!authenticated)
                Either.Left(InvalidAuthCredentialsError())

            searchUser(query.emailOrUsername())?.let { user ->
                Either.Right(AuthUserResponse.fromAggregate(user))
            } ?: Either.Left(InvalidAuthCredentialsError())
        }
    }

    private fun searchUser(emailOrUsername: String) = UserCriteria.emailOrUsernameCriteria(emailOrUsername).let {
        searcher.search(it).firstOrNull()
    }
}
