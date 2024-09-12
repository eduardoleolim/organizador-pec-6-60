package org.eduardoleolim.organizadorpec660.shared.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun generateErrorsLog(error: Throwable) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
    val formattedDateTime: String = LocalDateTime.now().format(formatter)
    val logFileName = "error_log_$formattedDateTime.log"

    File(AppConfig.getLogsDirectory()).resolve(logFileName).apply {
        parentFile.mkdirs()
        writeText(error.stackTraceToString())
    }
}
