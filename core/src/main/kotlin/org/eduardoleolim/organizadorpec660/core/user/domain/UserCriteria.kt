package org.eduardoleolim.organizadorpec660.core.user.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*

object UserCriteria {
    fun emailOrUsernameCriteria(emailOrUsername: String) = Criteria(
        Filters.none(),
        Filters(
            listOf(
                Filter(FilterField("email"), FilterOperator.EQUAL, FilterValue(emailOrUsername)),
                Filter(FilterField("username"), FilterOperator.EQUAL, FilterValue(emailOrUsername))
            )
        ),
        Orders.none(),
        null,
        null
    )
}
