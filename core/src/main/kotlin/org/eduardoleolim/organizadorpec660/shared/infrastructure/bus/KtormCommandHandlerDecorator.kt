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
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.ktorm.database.Database

class KtormCommandHandlerDecorator<L, R, T : Command<L, R>>(
    private val database: Database,
    private val commandHandler: CommandHandler<L, R, T>
) : CommandHandler<L, R, T> {
    override fun handle(command: T): Either<L, R> {
        return database.useTransaction { transaction ->
            commandHandler.handle(command).onLeft {
                transaction.rollback()
            }
        }
    }
}
