package org.eduardoleolim.organizadorpec660.shared.domain.criteria

import org.eduardoleolim.organizadorpec660.shared.domain.InvalidArgumentError

enum class FilterOperator(val value: String) {
    EQUAL("="),
    NOT_EQUAL("!="),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    CONTAINS("CONTAINS"),
    NOT_CONTAINS("NOT_CONTAINS");

    fun isPositive() = this != NOT_EQUAL && this != NOT_CONTAINS

    companion object {
        fun fromValue(value: String) = when (value) {
            "=" -> EQUAL
            "!=" -> NOT_EQUAL
            ">" -> GT
            ">=" -> GTE
            "<" -> LT
            "<=" -> LTE
            "CONTAINS" -> CONTAINS
            "NOT_CONTAINS" -> NOT_CONTAINS
            else -> throw InvalidArgumentError()
        }
    }
}
