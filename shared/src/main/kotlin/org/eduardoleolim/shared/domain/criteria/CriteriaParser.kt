package org.eduardoleolim.shared.domain.criteria

interface CriteriaParser<T> {
    fun parse(source: String, criteria: Criteria): T
}
