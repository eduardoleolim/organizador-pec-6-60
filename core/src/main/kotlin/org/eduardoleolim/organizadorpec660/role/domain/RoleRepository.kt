package org.eduardoleolim.organizadorpec660.role.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

interface RoleRepository {
    fun matching(criteria: Criteria): List<Role>
}
