package org.eduardoleolim.organizadorpec660.shared.infrastructure.koin

import org.koin.core.logger.PrintLogger
import org.koin.dsl.koinApplication
import org.ktorm.database.Database

class KtormAppKoinContext(database: Database, instrumentsPath: String) {
    val koinApp = koinApplication {
        logger(PrintLogger())
        modules(KtormAppModule.buildModule(database, instrumentsPath))
    }

    val koin = koinApp.koin
}
