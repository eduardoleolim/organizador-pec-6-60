package org.eduardoleolim.organizadorpec660.agency.model

sealed class AgencyFormState {
    data object Idle : AgencyFormState()
    data object InProgress : AgencyFormState()
    data object SuccessCreate : AgencyFormState()
    data object SuccessEdit : AgencyFormState()
    data class Error(val error: Throwable) : AgencyFormState()
}
