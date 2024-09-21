package org.eduardoleolim.organizadorpec660.federalEntity.model

sealed class FederalEntityFormState {
    data object Idle : FederalEntityFormState()
    data object InProgress : FederalEntityFormState()
    data object SuccessCreate : FederalEntityFormState()
    data object SuccessEdit : FederalEntityFormState()
    data class Error(val error: Throwable) : FederalEntityFormState()
}
