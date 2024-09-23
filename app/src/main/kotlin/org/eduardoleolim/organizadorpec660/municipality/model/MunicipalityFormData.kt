package org.eduardoleolim.organizadorpec660.municipality.model

import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse

data class MunicipalityFormData(
    val id: String? = null,
    val name: String = "",
    val keyCode: String = "",
    val federalEntity: FederalEntityResponse? = null
)
