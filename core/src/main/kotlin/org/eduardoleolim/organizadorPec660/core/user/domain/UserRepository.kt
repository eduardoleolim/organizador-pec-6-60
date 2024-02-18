package org.eduardoleolim.organizadorPec660.core.user.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

interface UserRepository {
    fun matching(criteria: Criteria): List<User>

    fun count(criteria: Criteria): Int

    fun save(user: User)

    fun delete(userId: String)
}
