package org.eduardoleolim.organizadorpec660.auth.application.authenticate

import org.eduardoleolim.organizadorpec660.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.auth.domain.InvalidAuthCredentialsError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.user.application.search.UserSearcher
import org.eduardoleolim.organizadorpec660.user.domain.UserCriteria

class AuthenticateUserQueryHandler(private val authenticator: UserAuthenticator, private val searcher: UserSearcher) :
    QueryHandler<AuthenticateUserQuery, AuthUserResponse> {
    override fun handle(query: AuthenticateUserQuery): AuthUserResponse {
        authenticator.authenticate(query.emailOrUsername(), query.password()).let { authenticated ->
            if (!authenticated)
                throw InvalidAuthCredentialsError()

            searchUser(query.emailOrUsername())?.let { user ->
                return AuthUserResponse.fromAggregate(user)
            } ?: throw InvalidAuthCredentialsError()
        }
    }

    private fun searchUser(emailOrUsername: String) = UserCriteria.emailOrUsernameCriteria(emailOrUsername).let {
        searcher.search(it).firstOrNull()
    }
}
