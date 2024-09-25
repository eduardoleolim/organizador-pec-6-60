package org.eduardoleolim.organizadorpec660.instrument.model

import com.seanproctor.datatable.paging.PaginatedDataTableState
import java.text.DateFormatSymbols
import java.time.LocalDate

data class InstrumentScreenState(
    val pageSizes: List<Int> = listOf(10, 25, 50, 100),
    val tableState: PaginatedDataTableState = PaginatedDataTableState(pageSizes.first(), 0),
    val statisticYears: List<Int> = (LocalDate.now().year downTo 1983).toList(),
    val statisticMonths: List<Pair<Int, String>> = DateFormatSymbols().months.take(12)
        .mapIndexed { index, month -> index + 1 to month.uppercase() }
)
