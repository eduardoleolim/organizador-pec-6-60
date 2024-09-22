package org.eduardoleolim.organizadorpec660.federalEntity.model

data class FederalEntitySearchParameters(
    val search: String = "",
    val orders: List<HashMap<String, String>> = emptyList(),
    val limit: Int? = null,
    val offset: Int? = null
)
