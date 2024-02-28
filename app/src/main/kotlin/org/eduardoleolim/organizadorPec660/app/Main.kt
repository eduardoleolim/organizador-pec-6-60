package org.eduardoleolim.organizadorPec660.app

import org.eduardoleolim.organizadorPec660.app.main.App
import org.eduardoleolim.organizadorPec660.app.shared.utils.AppUtils

fun main(args: Array<String>) {
    try {
        System.setProperty("skiko.renderApi", "OPENGL")
        val databasePath = AppUtils.databasePath(args) ?: throw Exception("Database path not found")
        val sqlitePassword = AppUtils.sqlitePassword() ?: throw Exception("Database password not found")
        val sqliteExtensions = AppUtils.sqliteExtensions()

        App(databasePath, sqlitePassword, sqliteExtensions).start()
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
}
