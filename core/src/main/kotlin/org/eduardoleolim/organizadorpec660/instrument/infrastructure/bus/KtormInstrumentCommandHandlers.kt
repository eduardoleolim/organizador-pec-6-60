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

package org.eduardoleolim.organizadorpec660.instrument.infrastructure.bus

import org.eduardoleolim.organizadorpec660.instrument.application.create.CreateInstrumentCommand
import org.eduardoleolim.organizadorpec660.instrument.application.create.CreateInstrumentCommandHandler
import org.eduardoleolim.organizadorpec660.instrument.application.create.InstrumentCreator
import org.eduardoleolim.organizadorpec660.instrument.application.delete.DeleteInstrumentCommand
import org.eduardoleolim.organizadorpec660.instrument.application.delete.DeleteInstrumentCommandHandler
import org.eduardoleolim.organizadorpec660.instrument.application.delete.InstrumentDeleter
import org.eduardoleolim.organizadorpec660.instrument.application.importer.ImportInstrumentsFromV1Command
import org.eduardoleolim.organizadorpec660.instrument.application.importer.ImportInstrumentsFromV1CommandHandler
import org.eduardoleolim.organizadorpec660.instrument.application.importer.InstrumentFromV1Importer
import org.eduardoleolim.organizadorpec660.instrument.application.save.*
import org.eduardoleolim.organizadorpec660.instrument.application.update.InstrumentUpdater
import org.eduardoleolim.organizadorpec660.instrument.application.update.UpdateInstrumentCommand
import org.eduardoleolim.organizadorpec660.instrument.application.update.UpdateInstrumentCommandHandler
import org.eduardoleolim.organizadorpec660.instrument.domain.AccdbInstrumentImportInput
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormCommandHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormInstrumentCommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>> = mapOf(
        CreateInstrumentCommand::class to createCommandHandler(),
        UpdateInstrumentCommand::class to updateCommandHandler(),
        UpdateInstrumentAsSavedCommand::class to updateAsSavedCommandHandler(),
        UpdateInstrumentAsNotSavedCommand::class to updateAsNotSavedCommandHandler(),
        DeleteInstrumentCommand::class to deleteCommandHandler(),
        ImportInstrumentsFromV1Command::class to importFromV1CommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val creator: InstrumentCreator by inject()
        val commandHandler = CreateInstrumentCommandHandler(creator)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: InstrumentUpdater by inject()
        val commandHandler = UpdateInstrumentCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateAsSavedCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: InstrumentSiresoSaver by inject()
        val commandHandler = UpdateInstrumentAsSavedCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateAsNotSavedCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: InstrumentSiresoSaver by inject()
        val commandHandler = UpdateInstrumentAsNotSavedCommandHandler(updater)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val deleter: InstrumentDeleter by inject()
        val commandHandler = DeleteInstrumentCommandHandler(deleter)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun importFromV1CommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val importer: InstrumentFromV1Importer<AccdbInstrumentImportInput> by inject()
        val commandHandler = ImportInstrumentsFromV1CommandHandler(importer)

        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
