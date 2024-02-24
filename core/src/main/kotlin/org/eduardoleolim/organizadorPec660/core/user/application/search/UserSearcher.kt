package org.eduardoleolim.organizadorPec660.core.user.application.search

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorPec660.core.user.domain.UserRepository

class UserSearcher(private val repository: UserRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}