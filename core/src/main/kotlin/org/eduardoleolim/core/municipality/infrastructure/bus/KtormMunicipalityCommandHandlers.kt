package org.eduardoleolim.core.municipality.infrastructure.bus

import org.eduardoleolim.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.core.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.core.municipality.application.create.CreateMunicipalityCommandHandler
import org.eduardoleolim.core.municipality.application.create.MunicipalityCreator
import org.eduardoleolim.core.municipality.infrastructure.persistence.KtormMunicipalityRepository
import org.eduardoleolim.core.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.shared.domain.bus.command.Command
import org.eduardoleolim.shared.domain.bus.command.CommandHandler
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormMunicipalityCommandHandlers(database: Database) :
    HashMap<KClass<out Command>, CommandHandler<out Command>>() {
    private val municipalityRepository: KtormMunicipalityRepository
    private val federalEntityRepository: KtormFederalEntityRepository

    init {
        municipalityRepository = KtormMunicipalityRepository(database)
        federalEntityRepository = KtormFederalEntityRepository(database)

        createCommandHandler(database)
    }

    private fun createCommandHandler(database: Database) {
        val creator = MunicipalityCreator(municipalityRepository, federalEntityRepository)
        val commandHandler = CreateMunicipalityCommandHandler(creator)

        KtormCommandHandlerDecorator(database, commandHandler).let {
            this[CreateMunicipalityCommand::class] = it
        }
    }
}
