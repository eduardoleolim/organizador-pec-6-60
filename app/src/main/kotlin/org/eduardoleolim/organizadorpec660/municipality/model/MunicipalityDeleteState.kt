package org.eduardoleolim.organizadorpec660.municipality.model

sealed class MunicipalityDeleteState {
    data object Idle : MunicipalityDeleteState()
    data object InProgress : MunicipalityDeleteState()
    data object Success : MunicipalityDeleteState()
    data class Error(val error: Throwable) : MunicipalityDeleteState()
}
