package org.eduardoleolim.organizadorpec660.core.user.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.OrFilters
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Orders
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.SingleFilter

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
