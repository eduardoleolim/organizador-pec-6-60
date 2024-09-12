package org.eduardoleolim.organizadorpec660.user.application.search

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.user.domain.UserRepository

class UserSearcher(private val repository: UserRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
