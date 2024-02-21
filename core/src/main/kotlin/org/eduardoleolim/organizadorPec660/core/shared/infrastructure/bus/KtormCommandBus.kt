package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.bus.KtormFederalEntityCommandHandlers
import org.eduardoleolim.organizadorPec660.core.instrumentType.infrastructure.bus.KtormInstrumentTypeCommandHandlers
import org.eduardoleolim.organizadorPec660.core.municipality.infrastructure.bus.KtormMunicipalityCommandHandlers
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.*
import org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.bus.KtormStatisticTypeCommandHandlers
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormCommandBus(database: Database, sqliteExtensions: List<String> = emptyList()) : CommandBus {
    private val commandHandlers: Map<KClass<out Command>, CommandHandler<out Command>> =
        HashMap<KClass<out Command>, CommandHandler<out Command>>().apply {
            putAll(KtormFederalEntityCommandHandlers(database, sqliteExtensions))
            putAll(KtormInstrumentTypeCommandHandlers(database, sqliteExtensions))
            putAll(KtormMunicipalityCommandHandlers(database, sqliteExtensions))
            putAll(KtormStatisticTypeCommandHandlers(database, sqliteExtensions))
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
