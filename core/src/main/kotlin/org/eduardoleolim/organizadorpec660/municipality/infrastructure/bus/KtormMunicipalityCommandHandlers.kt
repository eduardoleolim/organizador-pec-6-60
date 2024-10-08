/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.organizadorpec660.municipality.infrastructure.bus

import org.eduardoleolim.organizadorpec660.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.application.create.CreateMunicipalityCommandHandler
import org.eduardoleolim.organizadorpec660.municipality.application.create.MunicipalityCreator
import org.eduardoleolim.organizadorpec660.municipality.application.delete.DeleteMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.application.delete.DeleteMunicipalityCommandHandler
import org.eduardoleolim.organizadorpec660.municipality.application.delete.MunicipalityDeleter
import org.eduardoleolim.organizadorpec660.municipality.application.update.MunicipalityUpdater
import org.eduardoleolim.organizadorpec660.municipality.application.update.UpdateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.application.update.UpdateMunicipalityCommandHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormMunicipalityCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>> = mapOf(
        CreateMunicipalityCommand::class to createCommandHandler(),
        UpdateMunicipalityCommand::class to updateCommandHandler(),
        DeleteMunicipalityCommand::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val creator: MunicipalityCreator by inject()
        val commandHandler = CreateMunicipalityCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: MunicipalityUpdater by inject()
        val commandHandler = UpdateMunicipalityCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val deleter: MunicipalityDeleter by inject()
        val commandHandler = DeleteMunicipalityCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
