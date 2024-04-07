package org.eduardoleolim.organizadorpec660.core.shared.domain.criteria

enum class OrderType(val type: String) {
    ASC("asc"), DESC("desc"), NONE("none");

    fun isNone() = this == NONE

    fun isAsc() = this == ASC
}
