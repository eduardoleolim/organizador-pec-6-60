package org.eduardoleolim.organizadorpec660.role.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

interface RoleRepository {
    fun matching(criteria: Criteria): List<Role>
}
