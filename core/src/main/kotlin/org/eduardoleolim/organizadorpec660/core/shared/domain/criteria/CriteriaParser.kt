package org.eduardoleolim.organizadorpec660.core.shared.domain.criteria

interface CriteriaParser<T> {
    fun parse(source: String, criteria: Criteria): T
}
