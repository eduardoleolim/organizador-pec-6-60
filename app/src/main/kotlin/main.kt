import org.eduardoleolim.organizadorpec660.app.App
import org.eduardoleolim.organizadorpec660.app.shared.utils.AppConfig
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    try {
        val dataDir = File(AppConfig.getDataDirectory())

        val databaseDir = AppConfig["app.database.dir"] ?: error("Database directory is not defined")
        val databasePassword = AppConfig["app.database.password"] ?: error("Database password is not defined")
        val databaseExtensionsDir =
            AppConfig["app.database.extensions.dir"] ?: error("Database extensions directory is not defined")
        val instrumentsDir = AppConfig["app.instruments.dir"] ?: error("Instrument files directory is not defined")
        val tempDir = AppConfig["app.temp.dir"] ?: error("Temp directory is not defined")

        val absoluteDatabaseDir = dataDir.resolve(databaseDir).normalize().absolutePath
        val absoluteInstrumentsDir = dataDir.resolve(instrumentsDir).normalize().absolutePath

        App(
            absoluteDatabaseDir,
            databasePassword,
            databaseExtensionsDir,
            absoluteInstrumentsDir,
            tempDir
        ).start()
    } catch (e: Exception) {
        println(e.localizedMessage)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        val formattedDateTime: String = LocalDateTime.now().format(formatter)
        val logFileName = "error_log_$formattedDateTime.log"

        File(AppConfig.getLogsDirectory()).resolve(logFileName).apply {
            parentFile.mkdirs()
            writeText(e.stackTraceToString())
        }
    }
}
