import org.eduardoleolim.organizadorPec660.app.App
import org.eduardoleolim.organizadorPec660.app.shared.utils.AppUtils

fun main(args: Array<String>) {
    try {
        val osName = System.getProperty("os.name").lowercase()

        val renderApi = when {
            osName.contains("win") -> "OPENGL"
            osName.contains("mac") -> "METAL"
            else -> "OPENGL"
        }

        System.setProperty("skiko.renderApi", renderApi)
        val databasePath = AppUtils.databasePath(args) ?: throw Exception("Database path not found")
        val sqlitePassword = AppUtils.sqlitePassword() ?: throw Exception("Database password not found")
        val sqliteExtensions = AppUtils.sqliteExtensions()

        App(databasePath, sqlitePassword, sqliteExtensions).start()
    } catch (e: Exception) {
        println(e.localizedMessage)
    }
}
