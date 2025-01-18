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

package org.eduardoleolim.organizadorpec660.statisticType.infrastructure.bus

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.statisticType.application.create.CreateStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.statisticType.application.create.CreateStatisticTypeCommandHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.create.StatisticTypeCreator
import org.eduardoleolim.organizadorpec660.statisticType.application.delete.DeleteStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.statisticType.application.delete.DeleteStatisticTypeCommandHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.delete.StatisticTypeDeleter
import org.eduardoleolim.organizadorpec660.statisticType.application.update.StatisticTypeUpdater
import org.eduardoleolim.organizadorpec660.statisticType.application.update.UpdateStatisticTypeCommand
import org.eduardoleolim.organizadorpec660.statisticType.application.update.UpdateStatisticTypeCommandHandler
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormStatisticTypeCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>> = mapOf(
        CreateStatisticTypeCommand::class to createCommandHandler(),
        UpdateStatisticTypeCommand::class to updateCommandHandler(),
        DeleteStatisticTypeCommand::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val creator: StatisticTypeCreator by inject()
        val commandHandler = CreateStatisticTypeCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: StatisticTypeUpdater by inject()
        val commandHandler = UpdateStatisticTypeCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val deleter: StatisticTypeDeleter by inject()
        val commandHandler = DeleteStatisticTypeCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
