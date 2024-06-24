package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.agency.infrastructure.bus.KtormAgencyCommandHandlers
import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.bus.KtormFederalEntityCommandHandlers
import org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.bus.KtormMunicipalityCommandHandlers
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.bus.KtormStatisticTypeCommandHandlers
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormCommandBus(database: Database, instrumentsPath: String) : CommandBus {
    private val context = KtormAppKoinContext(database, instrumentsPath)

    private val commandHandlers = HashMap<KClass<out Command>, CommandHandler<out Command>>().apply {
        putAll(KtormAgencyCommandHandlers(context).handlers)
        putAll(KtormMunicipalityCommandHandlers(context).handlers)
        putAll(KtormStatisticTypeCommandHandlers(context).handlers)
        putAll(KtormFederalEntityCommandHandlers(context).handlers)
    }

    override fun dispatch(command: Command) {
        try {
            commandHandlers[command::class]?.let {
                @Suppress("UNCHECKED_CAST")
                it as CommandHandler<Command>
                it.handle(command)
            } ?: throw CommandNotRegisteredError(command::class)
        } catch (e: Exception) {
            throw CommandHandlerExecutionError(e)
        }
    }
}
