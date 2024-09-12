package org.eduardoleolim.organizadorpec660.user.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

interface UserRepository {
    fun matching(criteria: Criteria): List<User>

    fun count(criteria: Criteria): Int

    fun save(user: User)

    fun delete(userId: String)
}
