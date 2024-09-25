package org.eduardoleolim.organizadorpec660.instrument.model

import org.eduardoleolim.organizadorpec660.agency.application.SimpleAgencyResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse

data class InstrumentFormData(
    val id: String? = null,
    val statisticYear: Int? = null,
    val statisticMonth: Pair<Int, String>? = null,
    val statisticType: StatisticTypeResponse? = null,
    val federalEntity: FederalEntityResponse? = null,
    val municipality: SimpleMunicipalityResponse? = null,
    val agency: SimpleAgencyResponse? = null,
    val instrumentFilePath: String? = null
)
