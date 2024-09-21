package org.eduardoleolim.organizadorpec660.federalEntity.model

sealed class FederalEntityDeleteState {
    data object Idle : FederalEntityDeleteState()
    data object InProgress : FederalEntityDeleteState()
    data object Success : FederalEntityDeleteState()
    data class Error(val error: Throwable) : FederalEntityDeleteState()
}
