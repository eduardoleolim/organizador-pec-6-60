package org.eduardoleolim.organizadorPec660.core.role.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

interface RoleRepository {
    fun matching(criteria: Criteria): List<Role>
}
