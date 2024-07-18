package org.eduardoleolim.organizadorpec660.core.instrument.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.AndFilters
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Orders
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.SingleFilter

enum class InstrumentFields(val value: String) {
    Id("id"),
    StatisticYear("statisticYear"),
    StatisticMonth("statisticMonth"),
    Saved("saved"),
    AgencyId("agency.id"),
    AgencyConsecutive("agency.consecutive"),
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
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String
    ) = Criteria(
        AndFilters(
            SingleFilter.notEqual(InstrumentFields.Id.value, instrumentId),
            SingleFilter.equal(InstrumentFields.StatisticYear.value, statisticYear.toString()),
            SingleFilter.equal(InstrumentFields.StatisticMonth.value, statisticMonth.toString()),
            SingleFilter.equal(InstrumentFields.AgencyId.value, agencyId),
            SingleFilter.equal(InstrumentFields.StatisticTypeId.value, statisticTypeId),
            SingleFilter.equal(InstrumentFields.MunicipalityId.value, municipalityId)
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
