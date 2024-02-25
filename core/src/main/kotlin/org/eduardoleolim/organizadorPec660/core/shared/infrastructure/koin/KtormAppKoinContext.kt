package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.koin

import org.koin.core.logger.PrintLogger
import org.koin.dsl.koinApplication
import org.ktorm.database.Database

class KtormAppKoinContext(database: Database) {
    val koinApp = koinApplication {
        logger(PrintLogger())
        modules(KtormAppModule.buildModule(database))
    }

    val koin = koinApp.koin
}
