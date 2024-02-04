package org.eduardoleolim.organizadorpec660.core.user.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*

object UserCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal("id", id), Orders.none(), null, null)

    fun emailOrUsernameCriteria(emailOrUsername: String) = Criteria(
        OrFilters(
            listOf(
                SingleFilter.equal("email", emailOrUsername),
                SingleFilter.equal("username", emailOrUsername)
            )
        ),
        Orders.none(),
        null,
        null
    )
}
