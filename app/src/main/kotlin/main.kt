import org.eduardoleolim.organizadorpec660.app.App
import org.eduardoleolim.organizadorpec660.app.shared.utils.AppConfig

fun main() {
    try {
        val databaseDirectory = AppConfig["database.dir"] ?: error("Database path not found")
        val password = AppConfig["database.password"] ?: error("Database password not found")
        val extensionsDirectory = AppConfig["database.extensions.dir"] ?: error("Database extension path not found")
        val instrumentsDirectory = AppConfig["app.instruments.dir"] ?: error("Instruments path not found")
        val tempDirectory = AppConfig["app.temp.dir"] ?: run {
            val defaultTempDir = "\${java.io.tmpdir}/organizador-pec-6-60"
            AppConfig["app.temp.dir"] = defaultTempDir
            defaultTempDir
        }

        App(databaseDirectory, password, extensionsDirectory, instrumentsDirectory, tempDirectory).start()
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
}
