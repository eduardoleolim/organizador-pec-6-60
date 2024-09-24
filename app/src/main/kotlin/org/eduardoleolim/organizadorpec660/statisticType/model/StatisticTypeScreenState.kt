package org.eduardoleolim.organizadorpec660.statisticType.model

import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse

data class StatisticTypeScreenState(
    val pageSizes: List<Int> = listOf(10, 25, 50, 100),
    val tableState: PaginatedDataTableState = PaginatedDataTableState(pageSizes.first(), 0),
    val selectedStatisticType: StatisticTypeResponse? = null,
    val showFormModal: Boolean = false,
    val showDeleteModal: Boolean = false
)
