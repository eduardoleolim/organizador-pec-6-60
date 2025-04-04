/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

fun generateErrorsLog(scope: String = "", errors: List<Throwable>) {
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
        errors.forEach { writeText(it.stackTraceToString()) }
    }
}
