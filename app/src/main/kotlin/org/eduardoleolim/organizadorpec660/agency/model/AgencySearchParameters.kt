package org.eduardoleolim.organizadorpec660.agency.model

data class AgencySearchParameters(
    val search: String = "",
    val orders: List<HashMap<String, String>> = emptyList(),
    val limit: Int? = null,
    val offset: Int? = null
)
