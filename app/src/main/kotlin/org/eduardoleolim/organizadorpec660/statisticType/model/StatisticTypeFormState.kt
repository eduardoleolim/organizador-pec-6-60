package org.eduardoleolim.organizadorpec660.statisticType.model

sealed class StatisticTypeFormState {
    data object Idle : StatisticTypeFormState()
    data object InProgress : StatisticTypeFormState()
    data object SuccessCreate : StatisticTypeFormState()
    data object SuccessEdit : StatisticTypeFormState()
    data class Error(val error: Throwable) : StatisticTypeFormState()
}
