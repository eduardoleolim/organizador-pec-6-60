/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
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

import org.eduardoleolim.organizadorpec660.App
import org.eduardoleolim.organizadorpec660.shared.utils.AppConfig
import org.eduardoleolim.organizadorpec660.shared.utils.generateErrorsLog
import java.io.File

fun main() {
    try {
        val dataDir = File(AppConfig.dataDirectory)
        val databaseDir = AppConfig["app.database.dir"] ?: error("Database directory is not defined")
        val databasePassword = AppConfig["app.database.password"] ?: error("Database password is not defined")
        val databaseExtensionsDir =
            AppConfig["app.database.extensions.dir"] ?: error("Database extensions directory is not defined")
        val instrumentsDir = AppConfig["app.instruments.dir"] ?: error("Instrument files directory is not defined")
        val tempDir = AppConfig["app.temp.dir"] ?: error("Temp directory is not defined")
        val absoluteDatabaseDir = dataDir.resolve(databaseDir).normalize().absolutePath
        val absoluteInstrumentsDir = dataDir.resolve(instrumentsDir).normalize().absolutePath

        App(absoluteDatabaseDir, databasePassword, databaseExtensionsDir, absoluteInstrumentsDir, tempDir).start()
    } catch (e: Exception) {
        println(e.localizedMessage)
        generateErrorsLog("main", e)
    }
}
