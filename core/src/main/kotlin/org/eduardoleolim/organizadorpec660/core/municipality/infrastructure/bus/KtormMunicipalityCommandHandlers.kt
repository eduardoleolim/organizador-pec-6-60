package org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.create.CreateMunicipalityCommandHandler
import org.eduardoleolim.organizadorpec660.core.municipality.application.create.MunicipalityCreator
import org.eduardoleolim.organizadorpec660.core.municipality.application.delete.DeleteMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.delete.DeleteMunicipalityCommandHandler
import org.eduardoleolim.organizadorpec660.core.municipality.application.delete.MunicipalityDeleter
import org.eduardoleolim.organizadorpec660.core.municipality.application.update.MunicipalityUpdater
import org.eduardoleolim.organizadorpec660.core.municipality.application.update.UpdateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.update.UpdateMunicipalityCommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormMunicipalityCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command>, CommandHandler<out Command>> = mapOf(
        CreateMunicipalityCommand::class to createCommandHandler(),
        UpdateMunicipalityCommand::class to updateCommandHandler(),
        DeleteMunicipalityCommand::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<out Command> {
        val creator: MunicipalityCreator by inject()
        val commandHandler = CreateMunicipalityCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<out Command> {
        val updater: MunicipalityUpdater by inject()
        val commandHandler = UpdateMunicipalityCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<out Command> {
        val deleter: MunicipalityDeleter by inject()
        val commandHandler = DeleteMunicipalityCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
