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

package org.eduardoleolim.organizadorpec660.agency.infrastructure.bus

import org.eduardoleolim.organizadorpec660.agency.application.create.AgencyCreator
import org.eduardoleolim.organizadorpec660.agency.application.create.CreateAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.create.CreateAgencyCommandHandler
import org.eduardoleolim.organizadorpec660.agency.application.delete.AgencyDeleter
import org.eduardoleolim.organizadorpec660.agency.application.delete.DeleteAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.delete.DeleteAgencyCommandHandler
import org.eduardoleolim.organizadorpec660.agency.application.update.AgencyUpdater
import org.eduardoleolim.organizadorpec660.agency.application.update.UpdateAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.update.UpdateAgencyCommandHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormAgencyCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>> = mapOf(
        CreateAgencyCommand::class to createCommandHandler(),
        UpdateAgencyCommand::class to updateCommandHandler(),
        DeleteAgencyCommand::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val creator: AgencyCreator by inject()
        val commandHandler = CreateAgencyCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: AgencyUpdater by inject()
        val commandHandler = UpdateAgencyCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val deleter: AgencyDeleter by inject()
        val commandHandler = DeleteAgencyCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
