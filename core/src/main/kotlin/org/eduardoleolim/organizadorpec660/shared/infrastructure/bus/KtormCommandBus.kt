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

package org.eduardoleolim.organizadorpec660.shared.infrastructure.bus

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.infrastructure.bus.KtormAgencyCommandHandlers
import org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.bus.KtormFederalEntityCommandHandlers
import org.eduardoleolim.organizadorpec660.instrument.infrastructure.bus.KtormInstrumentCommandHandlers
import org.eduardoleolim.organizadorpec660.municipality.infrastructure.bus.KtormMunicipalityCommandHandlers
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandNotRegisteredError
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.statisticType.infrastructure.bus.KtormStatisticTypeCommandHandlers
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormCommandBus(database: Database, instrumentsPath: String) : CommandBus {
    private val context = KtormAppKoinContext(database, instrumentsPath)

    private val commandHandlers = HashMap<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>>().apply {
        putAll(KtormAgencyCommandHandlers(context).handlers)
        putAll(KtormFederalEntityCommandHandlers(context).handlers)
        putAll(KtormInstrumentCommandHandlers(context).handlers)
        putAll(KtormMunicipalityCommandHandlers(context).handlers)
        putAll(KtormStatisticTypeCommandHandlers(context).handlers)
    }

    override fun <L, R> dispatch(command: Command<L, R>): Either<L, R> {
        val commandHandler = commandHandlers[command::class]

        if (commandHandler != null) {
            @Suppress("UNCHECKED_CAST")
            commandHandler as CommandHandler<L, R, Command<L, R>>

            return commandHandler.handle(command)
        } else {
            throw CommandNotRegisteredError(command::class)
        }
    }
}
