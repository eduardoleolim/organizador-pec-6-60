package org.eduardoleolim.organizadorpec660.core.user.application.search

import org.eduardoleolim.organizadorpec660.core.user.domain.UserRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

class UserSearcher(private val repository: UserRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
