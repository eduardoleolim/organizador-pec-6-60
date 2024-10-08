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

package org.eduardoleolim.organizadorpec660.agency.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.*
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentCriteria
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentRepository

class AgencyDeleter(
    private val agencyRepository: AgencyRepository,
    private val instrumentRepository: InstrumentRepository
) {
    fun delete(id: String): Either<AgencyError, Unit> {
        try {
            if (exists(id).not())
                return Either.Left(AgencyNotFoundError(id))

            if (hasInstruments(id))
                return Either.Left(AgencyHasInstrumentsError())

            agencyRepository.delete(id)
            return Either.Right(Unit)
        } catch (e: InvalidArgumentAgencyException) {
            return Either.Left(CanNotDeleteAgencyError(e))
        }
    }

    private fun exists(id: String) = AgencyCriteria.idCriteria(id).let {
        agencyRepository.count(it) > 0
    }

    private fun hasInstruments(agencyId: String) = InstrumentCriteria.agencyCriteria(agencyId).let {
        instrumentRepository.count(it) > 0
    }
}
