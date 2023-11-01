package org.eduardoleolim.organizadorpec660.app

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.window.application
import org.eduardoleolim.organizadorpec660.app.utils.ArgsUtils
import org.eduardoleolim.organizadorpec660.app.views.MainWindow
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandBus
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormQueryBus
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.SqliteKtormDatabase
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

class App(private val commandBus: CommandBus, private val queryBus: QueryBus) {
    fun start() = application {
        MainWindow(
            title = "Organizador PEC-6-60",
            onCloseRequest = ::exitApplication,
            minHeight = 400,
            minWidth = 600,
        ) {
            Row {

            }
        }
    }
}

fun main(args: Array<String>) {
    val databasePath = ArgsUtils.getDatabasePath(args) ?: throw Exception("Database path not found")
    val ktormDatabase = SqliteKtormDatabase(databasePath)
    val commandBus = KtormCommandBus(ktormDatabase.init())
    val queryBus = KtormQueryBus(ktormDatabase.init(true))

    App(commandBus, queryBus).start()
}
