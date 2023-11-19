package org.eduardoleolim.organizadorpec660.app

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.application
import org.eduardoleolim.organizadorpec660.app.utils.ArgsUtils
import org.eduardoleolim.organizadorpec660.app.views.DarkColors
import org.eduardoleolim.organizadorpec660.app.views.LightColors
import org.eduardoleolim.organizadorpec660.app.views.MainWindow
import org.eduardoleolim.organizadorpec660.app.views.TitleBar
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandBus
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormQueryBus
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.SqliteKtormDatabase
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

class App(private val commandBus: CommandBus, private val queryBus: QueryBus) {
    fun start() = application {
        MainWindow(
            title = "Organizador PEC-6-60",
            icon = painterResource("assets/icon.ico"),
            onCloseRequest = ::exitApplication,
            isDarkTheme = false,
            lightColorScheme = LightColors,
            darkColorScheme = DarkColors,
            minHeight = 600,
            minWidth = 800,
        ) {
            Column {
                TitleBar(window, ::exitApplication)
            }
        }
    }
}

fun main(args: Array<String>) {
    try {
        val databasePath = ArgsUtils.getDatabasePath(args) ?: throw Exception("Database path not found")
        val commandBus = KtormCommandBus(SqliteKtormDatabase.connect(databasePath))
        val queryBus = KtormQueryBus(SqliteKtormDatabase.connect(databasePath, true))

        App(commandBus, queryBus).start()
    } catch (e: Exception) {
        println(e.message)
    }
}
