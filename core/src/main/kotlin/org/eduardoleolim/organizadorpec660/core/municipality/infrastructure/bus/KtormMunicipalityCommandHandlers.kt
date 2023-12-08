package org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.create.CreateMunicipalityCommandHandler
import org.eduardoleolim.organizadorpec660.core.municipality.application.create.MunicipalityCreator
import org.eduardoleolim.organizadorpec660.core.municipality.application.delete.DeleteMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.delete.DeleteMunicipalityCommandHandler
import org.eduardoleolim.organizadorpec660.core.municipality.application.delete.MunicipalityDeleter
import org.eduardoleolim.organizadorpec660.core.municipality.application.update.MunicipalityUpdater
import org.eduardoleolim.organizadorpec660.core.municipality.application.update.UpdateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.update.UpdateMunicipalityCommandHandler
import org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.persistence.KtormMunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormMunicipalityCommandHandlers(database: Database) :
    HashMap<KClass<out Command>, CommandHandler<out Command>>() {
    private val municipalityRepository: KtormMunicipalityRepository
    private val federalEntityRepository: KtormFederalEntityRepository

    init {
        municipalityRepository = KtormMunicipalityRepository(database)
        federalEntityRepository = KtormFederalEntityRepository(database)

        this[CreateMunicipalityCommand::class] = createCommandHandler(database)
        this[UpdateMunicipalityCommand::class] = updateCommandHandler(database)
        this[DeleteMunicipalityCommand::class] = deleteCommandHandler(database)
    }

    private fun createCommandHandler(database: Database): CommandHandler<out Command> {
        val creator = MunicipalityCreator(municipalityRepository, federalEntityRepository)
        val commandHandler = CreateMunicipalityCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(database: Database): CommandHandler<out Command> {
        val updater = MunicipalityUpdater(municipalityRepository, federalEntityRepository)
        val commandHandler = UpdateMunicipalityCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(database: Database): CommandHandler<out Command> {
        val deleter = MunicipalityDeleter(municipalityRepository)
        val commandHandler = DeleteMunicipalityCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
