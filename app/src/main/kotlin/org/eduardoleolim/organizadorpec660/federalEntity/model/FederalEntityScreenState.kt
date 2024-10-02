package org.eduardoleolim.organizadorpec660.federalEntity.model

import com.seanproctor.datatable.paging.PaginatedDataTableState
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse

data class FederalEntityScreenState(
    val pageSizes: List<Int> = listOf(10, 25, 50, 100),
    val tableState: PaginatedDataTableState = PaginatedDataTableState(pageSizes.first(), 0),
    val selectedFederalEntity: FederalEntityResponse? = null,
    val showFormModal: Boolean = false,
    val showDeleteModal: Boolean = false,
    val showImportExportSelector: Boolean = false,
    val showImportModal: Boolean = false,
    val showExportModal: Boolean = false
)
