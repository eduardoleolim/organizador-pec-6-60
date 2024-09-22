package org.eduardoleolim.organizadorpec660.agency.model

import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse

data class AgencyScreenState(
    val search: String = "",
    val pageSizes: List<Int> = listOf(10, 25, 50, 100),
    val tableState: PaginatedDataTableState = PaginatedDataTableState(pageSizes.first(), 0),
    val selectedAgency: AgencyResponse? = null,
    val showFormModal: Boolean = false,
    val showDeleteModal: Boolean = false,
)
