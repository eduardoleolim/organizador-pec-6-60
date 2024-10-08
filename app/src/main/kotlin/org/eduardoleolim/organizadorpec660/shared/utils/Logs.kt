package org.eduardoleolim.organizadorpec660.shared.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun generateErrorsLog(scope: String = "", error: Throwable) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    val formattedDateTime = LocalDateTime.now().format(formatter)

    val logFileName = buildString {
        append("error_log")
        if (scope.isNotBlank()) {
            append("_${scope.lowercase()}")
        }
        append("_$formattedDateTime.log")
    }

    File(AppConfig.logsDirectory).resolve(logFileName).apply {
        parentFile.mkdirs()
        writeText(error.stackTraceToString())
    }
}
