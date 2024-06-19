package org.eduardoleolim.organizadorpec660.core.agency.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*

enum class AgencyFields(val value: String) {
    Id("id"),
    Name("name"),
    Consecutive("consecutive"),
    MunicipalityId("municipality.id"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}


object AgencyCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal(AgencyFields.Id.value, id), Orders.none(), 1, null)

    fun anotherConsecutive(consecutive: String, municipalityOwnerId: String) = Criteria(
        AndFilters(
            listOf(
                SingleFilter.equal(AgencyFields.Consecutive.value, consecutive),
                SingleFilter.equal(AgencyFields.MunicipalityId.value, municipalityOwnerId)
            )
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
                    listOf(
                        SingleFilter.contains(AgencyFields.Name.value, it),
                        SingleFilter.contains(AgencyFields.Consecutive.value, it)
                    )
                )
            } ?: EmptyFilters(),
            orders?.let {
                Orders.fromValues(orders)
            } ?: Orders(listOf(Order.asc(FederalEntityFields.KeyCode.value))),
            limit,
            offset
        )
}
