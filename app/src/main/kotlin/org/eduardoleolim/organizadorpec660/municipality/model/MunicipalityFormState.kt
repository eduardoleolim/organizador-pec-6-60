package org.eduardoleolim.organizadorpec660.municipality.model

sealed class MunicipalityFormState {
    data object Idle : MunicipalityFormState()
    data object InProgress : MunicipalityFormState()
    data object SuccessCreate : MunicipalityFormState()
    data object SuccessEdit : MunicipalityFormState()
    data class Error(val error: Throwable) : MunicipalityFormState()
}
