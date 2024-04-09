package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.bus.KtormFederalEntityCommandHandlers
import org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.bus.KtormInstrumentTypeCommandHandlers
import org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.bus.KtormMunicipalityCommandHandlers
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.bus.KtormStatisticTypeCommandHandlers
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormCommandBus(database: Database) : CommandBus {
    private val context = KtormAppKoinContext(database)

    private val commandHandlers = HashMap<KClass<out Command>, CommandHandler<out Command>>().apply {
        putAll(KtormMunicipalityCommandHandlers(context).handlers)
        putAll(KtormInstrumentTypeCommandHandlers(context).handlers)
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