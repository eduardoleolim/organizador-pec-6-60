package org.eduardoleolim.organizadorpec660.app

import org.eduardoleolim.organizadorpec660.app.main.App
import org.eduardoleolim.organizadorpec660.app.shared.utils.ArgsUtils
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandBus
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormQueryBus
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.SqliteKtormDatabase

fun main(args: Array<String>) {
    try {
        val databasePath = ArgsUtils.databasePath(args) ?: throw Exception("Database path not found")
        val instrumentsPath = ArgsUtils.instrumentsPath(args) ?: throw Exception("Instruments path not found")
        val commandBus = KtormCommandBus(SqliteKtormDatabase.connect(databasePath))
        val queryBus = KtormQueryBus(SqliteKtormDatabase.connect(databasePath, true))

        App(commandBus, queryBus).start()
    } catch (e: Exception) {
        println(e.message)
    }
}
