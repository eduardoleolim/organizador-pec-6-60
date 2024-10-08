package org.eduardoleolim.organizadorpec660.auth.infrastructure.bus

import org.eduardoleolim.organizadorpec660.auth.application.authenticate.AuthenticateUserQuery
import org.eduardoleolim.organizadorpec660.auth.application.authenticate.AuthenticateUserQueryHandler
import org.eduardoleolim.organizadorpec660.auth.application.authenticate.UserAuthenticator
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.user.application.search.UserSearcher
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormAuthQueryHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>> = mapOf(
        AuthenticateUserQuery::class to authQueryHandler()
    )

    private fun authQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val authenticator: UserAuthenticator by inject()
        val searcher: UserSearcher by inject()
        val queryHandler = AuthenticateUserQueryHandler(authenticator, searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
