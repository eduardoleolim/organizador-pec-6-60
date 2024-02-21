package org.eduardoleolim.organizadorPec660.core.municipality.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.organizadorPec660.core.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.organizadorPec660.core.municipality.application.create.CreateMunicipalityCommandHandler
import org.eduardoleolim.organizadorPec660.core.municipality.application.create.MunicipalityCreator
import org.eduardoleolim.organizadorPec660.core.municipality.application.delete.DeleteMunicipalityCommand
import org.eduardoleolim.organizadorPec660.core.municipality.application.delete.DeleteMunicipalityCommandHandler
import org.eduardoleolim.organizadorPec660.core.municipality.application.delete.MunicipalityDeleter
import org.eduardoleolim.organizadorPec660.core.municipality.application.update.MunicipalityUpdater
import org.eduardoleolim.organizadorPec660.core.municipality.application.update.UpdateMunicipalityCommand
import org.eduardoleolim.organizadorPec660.core.municipality.application.update.UpdateMunicipalityCommandHandler
import org.eduardoleolim.organizadorPec660.core.municipality.infrastructure.persistence.KtormMunicipalityRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormMunicipalityCommandHandlers(private val database: Database) :
    HashMap<KClass<out Command>, CommandHandler<out Command>>() {
    private val municipalityRepository = KtormMunicipalityRepository(database)
    private val federalEntityRepository = KtormFederalEntityRepository(database)

    init {
        this[CreateMunicipalityCommand::class] = createCommandHandler()
        this[UpdateMunicipalityCommand::class] = updateCommandHandler()
        this[DeleteMunicipalityCommand::class] = deleteCommandHandler()
    }

    private fun createCommandHandler(): CommandHandler<out Command> {
        val creator = MunicipalityCreator(municipalityRepository, federalEntityRepository)
        val commandHandler = CreateMunicipalityCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<out Command> {
        val updater = MunicipalityUpdater(municipalityRepository, federalEntityRepository)
        val commandHandler = UpdateMunicipalityCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<out Command> {
        val deleter = MunicipalityDeleter(municipalityRepository)
        val commandHandler = DeleteMunicipalityCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
