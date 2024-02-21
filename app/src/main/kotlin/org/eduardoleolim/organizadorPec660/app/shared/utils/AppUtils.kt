package org.eduardoleolim.organizadorPec660.app.shared.utils

import java.io.File

object AppUtils {
    private val classLoader = Thread.currentThread().contextClassLoader

    fun databasePath(args: Array<String>): String? {
        return args.firstOrNull { it.startsWith("--database=") }?.replace("--database=", "")
    }

    fun sqlitePassword(): String? {
        return System.getProperty("sqlite.password")
    }

    fun sqliteExtensions(): List<String> {
        return System.getProperty("compose.application.resources.dir")?.let { resourcesPath ->
            File(resourcesPath).resolve("sqlite").listFiles()?.map { it.absolutePath }
        } ?: emptyList()
    }

    fun loadProperties(path: String) {
        classLoader.getResourceAsStream(path)?.use { stream ->
            stream.use { inputStream ->
                inputStream.bufferedReader().use {
                    System.getProperties().load(it)
                }
            }
        }
    }
}
