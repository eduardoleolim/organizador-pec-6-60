package org.eduardoleolim.organizadorpec660.instrument.model

import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse

data class InstrumentSearchParameters(
    val search: String = "",
    val statisticYear: Int? = null,
    val statisticMonth: Pair<Int, String>? = null,
    val statisticType: StatisticTypeResponse? = null,
    val federalEntity: FederalEntityResponse? = null,
    val municipality: MunicipalityResponse? = null,
    val agency: AgencyResponse? = null,
    val orders: List<HashMap<String, String>> = emptyList(),
    val limit: Int? = null,
    val offset: Int? = null
)
