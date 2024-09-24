package org.eduardoleolim.organizadorpec660.statisticType.model

sealed class StatisticTypeDeleteState {
    data object Idle : StatisticTypeDeleteState()
    data object InProgress : StatisticTypeDeleteState()
    data object Success : StatisticTypeDeleteState()
    data class Error(val message: String) : StatisticTypeDeleteState()
}
