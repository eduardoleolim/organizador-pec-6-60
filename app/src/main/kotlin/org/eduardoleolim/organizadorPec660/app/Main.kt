package org.eduardoleolim.organizadorPec660.app

import org.eduardoleolim.organizadorPec660.app.main.App
import org.eduardoleolim.organizadorPec660.app.shared.utils.ArgsUtils
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormCommandBus
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormQueryBus
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.SqliteKtormDatabase
import java.io.File

fun main(args: Array<String>) {
    try {
        System.setProperty("skiko.renderApi", "OPENGL")
        val sqliteExtensions = System.getProperty("compose.application.resources.dir")?.let { resourcesPath ->
            File(resourcesPath).resolve("sqlite").listFiles()?.map { it.absolutePath }
        } ?: emptyList<String>()

        val databasePath = ArgsUtils.databasePath(args) ?: throw Exception("Database path not found")
        val commandBus =
            KtormCommandBus(SqliteKtormDatabase.connect(databasePath, false, sqliteExtensions), sqliteExtensions)
        val queryBus =
            KtormQueryBus(SqliteKtormDatabase.connect(databasePath, true, sqliteExtensions), sqliteExtensions)

        App(commandBus, queryBus).start()
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
}
