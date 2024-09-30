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
