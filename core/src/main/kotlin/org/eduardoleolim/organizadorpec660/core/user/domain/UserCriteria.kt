package org.eduardoleolim.organizadorpec660.core.user.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.OrFilters
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Orders
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.SingleFilter

enum class UserFields(val value: String) {
    Id("id"),
    Firstname("firstname"),
    Lastname("Lastname"),
    Email("email"),
    Username("username"),
    RoleId("role.id"),
    RoleName("role.name"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}

object UserCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal(UserFields.Id.value, id), Orders.none(), null, null)

    fun emailOrUsernameCriteria(emailOrUsername: String) = Criteria(
        OrFilters(
            SingleFilter.equal(UserFields.Email.value, emailOrUsername),
            SingleFilter.equal(UserFields.Username.value, emailOrUsername)
        ),
        Orders.none(),
        null,
        null
    )
}
