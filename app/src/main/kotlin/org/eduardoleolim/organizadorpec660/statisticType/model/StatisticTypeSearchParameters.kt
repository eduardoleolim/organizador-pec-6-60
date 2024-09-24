package org.eduardoleolim.organizadorpec660.statisticType.model

data class StatisticTypeSearchParameters(
    val search: String = "",
    val orders: List<HashMap<String, String>> = emptyList(),
    val limit: Int? = null,
    val offset: Int? = null
)
