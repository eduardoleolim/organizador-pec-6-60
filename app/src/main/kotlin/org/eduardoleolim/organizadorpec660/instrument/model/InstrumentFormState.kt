package org.eduardoleolim.organizadorpec660.instrument.model

sealed class InstrumentFormState {
    data object Idle : InstrumentFormState()
    data object InProgress : InstrumentFormState()
    data object SuccessCreate : InstrumentFormState()
    data object SuccessEdit : InstrumentFormState()
    data class Error(val error: Throwable) : InstrumentFormState()
}
