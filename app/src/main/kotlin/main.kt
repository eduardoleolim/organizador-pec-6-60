import org.eduardoleolim.organizadorpec660.app.App
import org.eduardoleolim.organizadorpec660.app.shared.utils.AppConfig
import org.eduardoleolim.organizadorpec660.app.shared.utils.DesktopPlatform
import java.io.File

fun main() {
    try {
        val renderApi = when (DesktopPlatform.Current) {
            DesktopPlatform.MacOS -> "METAL"
            else -> "OPENGL"
        }

        System.setProperty("skiko.renderApi", renderApi)
        val databasePath = AppConfig.getProperty("database.path") ?: error("Database path not found")
        val password = AppConfig.getProperty("database.password") ?: error("Database password not found")
        val extensionsPath =
            AppConfig.getProperty("database.extensions.path") ?: error("Database extension path not found")
        val instrumentsPath = AppConfig.getProperty("instruments.path")?.let {
            File(System.getProperty("user.dir"), it).canonicalPath
        } ?: error("Instruments path not found")

        App(databasePath, password, extensionsPath, instrumentsPath).start()
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
}
