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

package org.eduardoleolim.organizadorpec660.instrument.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

interface InstrumentRepository {
    fun matching(criteria: Criteria): List<Instrument>

    fun searchFileById(id: String): InstrumentFile?

    fun searchInstrumentFile(instrumentFileId: String): InstrumentFile?

    fun count(criteria: Criteria): Int

    fun save(instrument: Instrument, instrumentFile: InstrumentFile? = null)

    fun delete(instrumentId: String)
}
