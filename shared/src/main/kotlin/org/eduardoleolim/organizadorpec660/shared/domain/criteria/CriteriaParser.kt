package org.eduardoleolim.organizadorpec660.shared.domain.criteria

interface CriteriaParser<T> {
    fun parse(source: String, criteria: Criteria): T
}
