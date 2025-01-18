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

package org.eduardoleolim.organizadorpec660.agency.application.update

import org.eduardoleolim.organizadorpec660.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command

class UpdateAgencyCommand(
    agencyId: String,
    name: String,
    consecutive: String,
    municipalityId: String,
    statisticTypeIds: List<String>
) : Command<AgencyError, Unit> {
    private val agencyId = agencyId.trim()
    private val name = name.trim().uppercase()
    private val consecutive = consecutive.trim()
    private val municipalityId = municipalityId.trim()
    private val statisticTypeIds = statisticTypeIds.distinct()

    fun agencyId(): String {
        return agencyId
    }

    fun name(): String {
        return name
    }

    fun consecutive(): String {
        return consecutive
    }

    fun municipalityId(): String {
        return municipalityId
    }

    fun statisticTypeIds(): List<String> {
        return statisticTypeIds
    }
}
