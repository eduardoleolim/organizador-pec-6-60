package org.eduardoleolim.app

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.window.application
import org.eduardoleolim.app.utils.ArgsUtils
import org.eduardoleolim.app.views.MainWindow
import org.eduardoleolim.core.shared.infrastructure.bus.KtormCommandBus
import org.eduardoleolim.core.shared.infrastructure.bus.KtormQueryBus
import org.eduardoleolim.core.shared.infrastructure.models.KtormDatabase
import org.eduardoleolim.shared.domain.bus.command.CommandBus
import org.eduardoleolim.shared.domain.bus.query.QueryBus

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
    val databasePath = ArgsUtils.getDatabasePath(args)?.let {
        "jdbc:sqlite:$it"
    } ?: throw Exception("Database path not found")

    val commandBus = KtormCommandBus(KtormDatabase.init(databasePath))
    val queryBus = KtormQueryBus(KtormDatabase.init(databasePath, true))

    App(commandBus, queryBus).start()
}
