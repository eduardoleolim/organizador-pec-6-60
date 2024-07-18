import org.eduardoleolim.organizadorpec660.app.App
import org.eduardoleolim.organizadorpec660.app.shared.utils.AppConfig

fun main() {
    try {
        val databasePath = AppConfig["database.path"] ?: error("Database path not found")
        val password = AppConfig["database.password"] ?: error("Database password not found")
        val extensionsPath = AppConfig["database.extensions.path"] ?: error("Database extension path not found")
        val instrumentsPath = AppConfig["instruments.path"] ?: error("Instruments path not found")

        App(databasePath, password, extensionsPath, instrumentsPath).start()
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
}
