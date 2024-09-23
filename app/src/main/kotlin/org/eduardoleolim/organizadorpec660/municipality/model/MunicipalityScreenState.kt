package org.eduardoleolim.organizadorpec660.municipality.model

import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse

data class MunicipalityScreenState(
    val pageSizes: List<Int> = listOf(10, 25, 50, 100),
    val tableState: PaginatedDataTableState = PaginatedDataTableState(pageSizes.first(), 0),
    val selectedMunicipality: MunicipalityResponse? = null,
    val showFormModal: Boolean = false,
    val showDeleteModal: Boolean = false
)
