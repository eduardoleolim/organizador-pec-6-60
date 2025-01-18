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

package org.eduardoleolim.organizadorpec660.instrument.application.importer

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.instrument.domain.AccdbInstrumentImportInput
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentImportWarning
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class ImportInstrumentsFromV1CommandHandler<I : AccdbInstrumentImportInput>(private val importer: InstrumentFromV1Importer<I>) :
    CommandHandler<InstrumentError, List<InstrumentImportWarning>, ImportInstrumentsFromV1Command<I>> {
    override fun handle(command: ImportInstrumentsFromV1Command<I>): Either<InstrumentError, List<InstrumentImportWarning>> {
        return importer.import(command.input(), command.overrideIfExists())
    }
}
