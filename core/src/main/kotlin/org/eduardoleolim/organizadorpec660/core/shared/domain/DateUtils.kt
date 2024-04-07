package org.eduardoleolim.organizadorpec660.core.shared.domain

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Converts a [Date] to a [LocalDateTime].
 * @return A [LocalDateTime] instance.
 */
fun Date.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())
}

/**
 * Converts a [LocalDateTime] to a [Date].
 * @return A [Date] instance.
 */
fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}
