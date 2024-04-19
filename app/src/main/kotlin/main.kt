import org.eduardoleolim.organizadorpec660.app.App
import org.eduardoleolim.organizadorpec660.app.shared.utils.AppConfig

fun main() {
    try {
        val osName = System.getProperty("os.name").lowercase()

        val renderApi = when {
            osName.contains("win") -> "OPENGL"
            osName.contains("mac") -> "METAL"
            else -> "OPENGL"
        }

        System.setProperty("skiko.renderApi", renderApi)
        val databasePath = AppConfig.getProperty("database.path") ?: throw Exception("Database path not found")
        val password = AppConfig.getProperty("database.password") ?: throw Exception("Database password not found")
        val extensionsPath =
            AppConfig.getProperty("database.extensions.path") ?: throw Exception("Database extension path not found")

        App(databasePath, password, extensionsPath).start()
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
}
