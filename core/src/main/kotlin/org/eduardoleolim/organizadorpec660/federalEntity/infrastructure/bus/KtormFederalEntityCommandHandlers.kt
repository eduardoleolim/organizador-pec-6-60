/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.bus

import org.eduardoleolim.organizadorpec660.federalEntity.application.create.CreateFederalEntityCommand
import org.eduardoleolim.organizadorpec660.federalEntity.application.create.CreateFederalEntityCommandHandler
import org.eduardoleolim.organizadorpec660.federalEntity.application.create.FederalEntityCreator
import org.eduardoleolim.organizadorpec660.federalEntity.application.delete.DeleteFederalEntityCommand
import org.eduardoleolim.organizadorpec660.federalEntity.application.delete.DeleteFederalEntityCommandHandler
import org.eduardoleolim.organizadorpec660.federalEntity.application.delete.FederalEntityDeleter
import org.eduardoleolim.organizadorpec660.federalEntity.application.importer.CsvImportFederalEntitiesCommand
import org.eduardoleolim.organizadorpec660.federalEntity.application.importer.CsvImportFederalEntitiesCommandHandler
import org.eduardoleolim.organizadorpec660.federalEntity.application.importer.FederalEntityImporter
import org.eduardoleolim.organizadorpec660.federalEntity.application.update.FederalEntityUpdater
import org.eduardoleolim.organizadorpec660.federalEntity.application.update.UpdateFederalEntityCommand
import org.eduardoleolim.organizadorpec660.federalEntity.application.update.UpdateFederalEntityCommandHandler
import org.eduardoleolim.organizadorpec660.federalEntity.domain.CsvFederalEntityImportInput
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormFederalEntityCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>> = mapOf(
        CreateFederalEntityCommand::class to createCommandHandler(),
        UpdateFederalEntityCommand::class to updateCommandHandler(),
        DeleteFederalEntityCommand::class to deleteCommandHandler(),
        CsvImportFederalEntitiesCommand::class to importFromCsvCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val creator: FederalEntityCreator by inject()
        val commandHandler = CreateFederalEntityCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: FederalEntityUpdater by inject()
        val commandHandler = UpdateFederalEntityCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val deleter: FederalEntityDeleter by inject()
        val commandHandler = DeleteFederalEntityCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun importFromCsvCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val importer: FederalEntityImporter<CsvFederalEntityImportInput> by inject()
        val commandHandler = CsvImportFederalEntitiesCommandHandler(importer)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
