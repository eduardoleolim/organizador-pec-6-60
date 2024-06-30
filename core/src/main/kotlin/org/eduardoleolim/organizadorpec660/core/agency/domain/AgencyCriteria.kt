package org.eduardoleolim.organizadorpec660.core.agency.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*

enum class AgencyFields(val value: String) {
    Id("id"),
    Name("name"),
    Consecutive("consecutive"),
    MunicipalityId("municipality.id"),
    MunicipalityKeyCode("municipality.keyCode"),
    MunicipalityName("municipality.name"),
    StatisticTypeId("statisticType.id"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}


object AgencyCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal(AgencyFields.Id.value, id), Orders.none(), 1, null)

    fun anotherConsecutiveCriteria(consecutive: String, municipalityId: String) = Criteria(
        AndFilters(
            SingleFilter.equal(AgencyFields.Consecutive.value, consecutive),
            SingleFilter.equal(AgencyFields.MunicipalityId.value, municipalityId)
        ),
        Orders.none(),
        1,
        null
    )

    fun anotherConsecutiveCriteria(agencyId: String, consecutive: String, municipalityOwnerId: String) = Criteria(
        AndFilters(
            SingleFilter.notEqual(AgencyFields.Id.value, agencyId),
            SingleFilter.equal(AgencyFields.Consecutive.value, consecutive),
            SingleFilter.equal(AgencyFields.MunicipalityId.value, municipalityOwnerId)
        ),
        Orders.none(),
        1,
        null
    )

    fun searchCriteria(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) =
        Criteria(
            search?.let {
                OrFilters(
                    SingleFilter.contains(AgencyFields.Name.value, it),
                    SingleFilter.contains(AgencyFields.Consecutive.value, it),
                    SingleFilter.contains(AgencyFields.MunicipalityName.value, it),
                    SingleFilter.contains(AgencyFields.MunicipalityKeyCode.value, it)
                )
            } ?: EmptyFilters(),
            orders?.let {
                val fields = AgencyFields.entries.map { it.value }
                val filteredOrders = orders.mapNotNull { it.takeIf { fields.contains(it["orderBy"]) } }

                Orders.fromValues(filteredOrders.toTypedArray())
            } ?: Orders(Order.asc(AgencyFields.Name.value)),
            limit,
            offset
        )

    fun statisticTypeCriteria(statisticTypeId: String) = Criteria(
        SingleFilter.equal(AgencyFields.StatisticTypeId.value, statisticTypeId),
        Orders.none(),
        null,
        null
    )
}
