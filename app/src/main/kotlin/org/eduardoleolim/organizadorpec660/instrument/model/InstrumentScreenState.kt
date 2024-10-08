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

package org.eduardoleolim.organizadorpec660.instrument.model

import com.seanproctor.datatable.paging.PaginatedDataTableState
import java.text.DateFormatSymbols
import java.time.LocalDate

data class InstrumentScreenState(
    val pageSizes: List<Int> = listOf(10, 25, 50, 100),
    val tableState: PaginatedDataTableState = PaginatedDataTableState(pageSizes.first(), 0),
    val statisticYears: List<Int> = (LocalDate.now().year downTo 1983).toList(),
    val statisticMonths: List<Pair<Int, String>> = DateFormatSymbols().months.take(12)
        .mapIndexed { index, month -> index + 1 to month.uppercase() },
    val showImportExportSelector: Boolean = false,
    val showImportModal: Boolean = false,
    val showExportModal: Boolean = false
)
