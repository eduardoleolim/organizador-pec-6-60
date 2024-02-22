package org.eduardoleolim.organizadorPec660.app

import org.eduardoleolim.organizadorPec660.app.main.App
import org.eduardoleolim.organizadorPec660.app.main.InitializeApp
import org.eduardoleolim.organizadorPec660.app.shared.utils.AppUtils
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormCommandBus
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormQueryBus
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.SqliteKtormDatabase

fun main(args: Array<String>) {
    try {
        System.setProperty("skiko.renderApi", "OPENGL")
        val databasePath = AppUtils.databasePath(args) ?: throw Exception("Database path not found")
        var sqlitePassword = AppUtils.sqlitePassword()
        val sqliteExtensions = AppUtils.sqliteExtensions()

        if (SqliteKtormDatabase.exists(databasePath).not()) {
            InitializeApp().start { password ->
                sqlitePassword = password
                AppUtils.sqlitePassword(password)
            }
        }

        val commandBus = KtormCommandBus(SqliteKtormDatabase.connect(databasePath, sqlitePassword!!, sqliteExtensions))
        val queryBus =
            KtormQueryBus(SqliteKtormDatabase.connectReadOnly(databasePath, sqlitePassword!!, sqliteExtensions))

        App(commandBus, queryBus).start()
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
}
