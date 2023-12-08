package org.eduardoleolim.organizadorpec660.core.user.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*

object UserCriteria {
    fun idCriteria(id: String) = Criteria(
        SingleFilter(Filter(FilterField("id"), FilterOperator.EQUAL, FilterValue(id))),
        Orders.none(),
        null,
        null
    )

    fun emailOrUsernameCriteria(emailOrUsername: String) = Criteria(
        OrFilters(
            listOf(
                SingleFilter(Filter(FilterField("email"), FilterOperator.EQUAL, FilterValue(emailOrUsername))),
                SingleFilter(Filter(FilterField("username"), FilterOperator.EQUAL, FilterValue(emailOrUsername)))
            )
        ),
        Orders.none(),
        null,
        null
    )
}
