package org.eduardoleolim.organizadorpec660.core.instrument.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*

enum class InstrumentFields(val value: String) {
    Id("id"),
    StatisticYear("statisticYear"),
    StatisticMonth("statisticMonth"),
    Saved("saved"),
    AgencyId("agency.id"),
    AgencyConsecutive("agency.consecutive"),
    StatisticTypeId("statisticType.id"),
    StatisticTypeKeyCode("statisticType.keyCode"),
    StatisticTypeName("statisticType.name"),
    FederalEntityId("federalEntity.id"),
    FederalEntityKeyCode("federalEntity.keyCode"),
    FederalEntityName("federalEntity.name"),
    MunicipalityId("municipality.id"),
    MunicipalityKeyCode("municipality.keyCode"),
    MunicipalityName("municipality.name"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}

object InstrumentCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal(InstrumentFields.Id.value, id), Orders.none(), 1, null)

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
        null,
        null
    )

    fun otherInstrumentCriteria(
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String
    ) = Criteria(
        AndFilters(
            SingleFilter.equal(InstrumentFields.StatisticYear.value, statisticYear.toString()),
            SingleFilter.equal(InstrumentFields.StatisticMonth.value, statisticMonth.toString()),
            SingleFilter.equal(InstrumentFields.AgencyId.value, agencyId),
            SingleFilter.equal(InstrumentFields.StatisticTypeId.value, statisticTypeId),
            SingleFilter.equal(InstrumentFields.MunicipalityId.value, municipalityId)
        ),
        Orders.none(),
        null,
        null
    )

    fun searchCriteria(
        agencyId: String? = null,
        statisticTypeId: String? = null,
        federalEntityId: String? = null,
        municipalityId: String? = null,
        year: Int? = null,
        month: Int? = null,
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = Criteria(
        AndFilters(
            AndFilters(
                agencyId?.let { SingleFilter.equal(InstrumentFields.AgencyId.value, it) } ?: EmptyFilters(),
                statisticTypeId?.let { SingleFilter.equal(InstrumentFields.StatisticTypeId.value, it) }
                    ?: EmptyFilters(),
                federalEntityId?.let { SingleFilter.equal(InstrumentFields.FederalEntityId.value, it) }
                    ?: EmptyFilters(),
                municipalityId?.let { SingleFilter.equal(InstrumentFields.MunicipalityId.value, it) } ?: EmptyFilters(),
                year?.let { SingleFilter.equal(InstrumentFields.StatisticYear.value, it.toString()) } ?: EmptyFilters(),
                month?.let { SingleFilter.equal(InstrumentFields.StatisticMonth.value, it.toString()) }
                    ?: EmptyFilters()
            ),
            search?.let {
                OrFilters(
                    SingleFilter.contains(InstrumentFields.StatisticYear.value, search),
                    SingleFilter.contains(InstrumentFields.StatisticMonth.value, search),
                    SingleFilter.contains(InstrumentFields.AgencyConsecutive.value, search),
                    SingleFilter.contains(InstrumentFields.StatisticTypeName.value, search),
                    SingleFilter.contains(InstrumentFields.StatisticTypeKeyCode.value, search),
                    SingleFilter.contains(InstrumentFields.FederalEntityName.value, search),
                    SingleFilter.contains(InstrumentFields.FederalEntityKeyCode.value, search),
                    SingleFilter.contains(InstrumentFields.MunicipalityName.value, search),
                    SingleFilter.contains(InstrumentFields.MunicipalityKeyCode.value, search),
                )
            } ?: EmptyFilters()
        ),
        orders?.let {
            val fields = InstrumentFields.entries.map { it.value }
            val filteredOrders = orders.mapNotNull { it.takeIf { fields.contains(it["orderBy"]) } }

            Orders.fromValues(filteredOrders.toTypedArray())
        } ?: Orders(
            Order.asc(InstrumentFields.FederalEntityKeyCode.value),
            Order.asc(InstrumentFields.MunicipalityKeyCode.value),
            Order.asc(InstrumentFields.StatisticYear.value),
            Order.asc(InstrumentFields.StatisticMonth.value)
        ),
        limit,
        offset
    )

    fun agencyCriteria(agencyId: String) = Criteria(
        SingleFilter.equal(InstrumentFields.AgencyId.value, agencyId),
        Orders.none(),
        null,
        null
    )
}
