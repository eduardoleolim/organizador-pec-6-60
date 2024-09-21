package org.eduardoleolim.organizadorpec660.agency.model

import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse

data class Agency(
    val id: String? = null,
    val name: String = "",
    val consecutive: String = "",
    val federalEntity: FederalEntityResponse? = null,
    val municipality: SimpleMunicipalityResponse? = null,
    val statisticTypes: List<StatisticTypeResponse> = emptyList(),
)
