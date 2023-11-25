package org.eduardoleolim.organizadorpec660.core.auth.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.auth.application.authenticate.AuthenticateUserQuery
import org.eduardoleolim.organizadorpec660.core.auth.application.authenticate.AuthenticateUserQueryHandler
import org.eduardoleolim.organizadorpec660.core.auth.application.authenticate.UserAuthenticator
import org.eduardoleolim.organizadorpec660.core.auth.infrastructure.persistence.KtormAuthRepository
import org.eduardoleolim.organizadorpec660.core.user.application.search.UserSearcher
import org.eduardoleolim.organizadorpec660.core.user.infrastructure.persistence.KtormUserRepository
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormAuthQueryHandlers(database: Database) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val authRepository: KtormAuthRepository
    private val userRepository: KtormUserRepository
    private val authenticator: UserAuthenticator
    private val searcher: UserSearcher

    init {
        authRepository = KtormAuthRepository(database)
        userRepository = KtormUserRepository(database)
        authenticator = UserAuthenticator(authRepository)
        searcher = UserSearcher(userRepository)

        this[AuthenticateUserQuery::class] = AuthenticateUserQueryHandler(authenticator, searcher)
    }
}
