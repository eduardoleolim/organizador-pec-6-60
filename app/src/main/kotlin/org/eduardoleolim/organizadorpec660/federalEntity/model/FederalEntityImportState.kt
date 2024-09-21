package org.eduardoleolim.organizadorpec660.federalEntity.model

sealed class FederalEntityImportState {
    data object Idle : FederalEntityImportState()
    data object InProgress : FederalEntityImportState()
    data class Success(val warnings: List<Throwable> = emptyList()) : FederalEntityImportState()
    data class Error(val error: Throwable) : FederalEntityImportState()
}
