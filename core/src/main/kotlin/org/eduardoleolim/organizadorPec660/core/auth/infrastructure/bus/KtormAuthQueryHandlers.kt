package org.eduardoleolim.organizadorPec660.core.auth.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.auth.application.authenticate.AuthenticateUserQuery
import org.eduardoleolim.organizadorPec660.core.auth.application.authenticate.AuthenticateUserQueryHandler
import org.eduardoleolim.organizadorPec660.core.auth.application.authenticate.UserAuthenticator
import org.eduardoleolim.organizadorPec660.core.auth.infrastructure.persistence.KtormAuthRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorPec660.core.user.application.search.UserSearcher
import org.eduardoleolim.organizadorPec660.core.user.infrastructure.persistence.KtormUserRepository
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormAuthQueryHandlers(private val database: Database) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val authRepository = KtormAuthRepository(database)
    private val userRepository = KtormUserRepository(database)
    private val authenticator = UserAuthenticator(authRepository)
    private val searcher = UserSearcher(userRepository)

    init {
        this[AuthenticateUserQuery::class] = authQueryHandler()
    }

    private fun authQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val queryHandler = AuthenticateUserQueryHandler(authenticator, searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
