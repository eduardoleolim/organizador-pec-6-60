package org.eduardoleolim.organizadorPec660.app.shared.utils

import java.io.File
import java.io.FileOutputStream
import java.util.*

object AppUtils {
    private val resourcesPath = System.getProperty("compose.application.resources.dir")

    init {
        loadProperties()
    }

    fun databasePath(args: Array<String>): String? {
        return args.firstOrNull { it.startsWith("--database=") }?.replace("--database=", "")
    }

    fun sqlitePassword(): String? {
        return System.getProperty("sqlite.password")
    }

    fun sqlitePassword(password: String) {
        System.setProperty("sqlite.password", password)

        val file = File(resourcesPath).resolve("app.properties")

        if (file.exists().not())
            throw Exception("app.properties not found")

        with(Properties()) {
            load(file.inputStream())
            setProperty("sqlite.password", password)
            store(FileOutputStream(file), null)
        }
    }

    fun sqliteExtensions(): List<String> {
        return File(resourcesPath).resolve("sqlite").listFiles()?.map { it.absolutePath } ?: emptyList()
    }

    private fun loadProperties() {
        val file = File(resourcesPath).resolve("app.properties")

        if (file.exists().not())
            throw Exception("app.properties not found")

        System.getProperties().load(file.inputStream())
    }
}
