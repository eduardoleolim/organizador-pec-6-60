package org.eduardoleolim.organizadorpec660.municipality.model

import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse

data class MunicipalitySearchParameters(
    val search: String = "",
    val federalEntity: FederalEntityResponse? = null,
    val orders: List<HashMap<String, String>> = emptyList(),
    val limit: Int? = null,
    val offset: Int? = null
)
