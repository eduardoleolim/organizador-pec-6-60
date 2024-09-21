package org.eduardoleolim.organizadorpec660.agency.model

sealed class AgencyDeleteState {
    data object Idle : AgencyDeleteState()
    data object InProgress : AgencyDeleteState()
    data object Success : AgencyDeleteState()
    data class Error(val message: String) : AgencyDeleteState()
}
