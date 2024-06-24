package org.eduardoleolim.organizadorpec660.core.instrument.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.AndFilters
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Orders
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.SingleFilter

enum class InstrumentFields(val value: String) {
    Id("id"),
    StatisticYear("statisticYear"),
    StatisticMonth("statisticMonth"),
    Consecutive("consecutive"),
    Saved("saved"),
    AgencyId("agency.id"),
    StatisticTypeId("statisticType.id"),
    MunicipalityId("municipality.id"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}

object InstrumentCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal("id", id), Orders.none(), 1, null)

    fun anotherInstrumentCriteria(
        instrumentId: String,
        statisticYear: Int,
        statisticMonth: Int,
        consecutive: String,
        statisticTypeId: String,
        municipalityId: String
    ) = Criteria(
        AndFilters(
            listOf(
                SingleFilter.notEqual(InstrumentFields.Id.value, instrumentId),
                SingleFilter.equal(InstrumentFields.StatisticYear.value, statisticYear.toString()),
                SingleFilter.equal(InstrumentFields.StatisticMonth.value, statisticMonth.toString()),
                SingleFilter.equal(InstrumentFields.Consecutive.value, consecutive),
                SingleFilter.equal(InstrumentFields.StatisticTypeId.value, statisticTypeId),
                SingleFilter.equal(InstrumentFields.MunicipalityId.value, municipalityId)
            )
        ),
        Orders.none(),
        1,
        null
    )

    fun agencyCriteria(agencyId: String) = Criteria(
        SingleFilter.equal(InstrumentFields.AgencyId.value, agencyId),
        Orders.none(),
        1,
        null
    )
}
