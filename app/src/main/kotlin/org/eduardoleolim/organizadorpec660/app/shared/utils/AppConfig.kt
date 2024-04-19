package org.eduardoleolim.organizadorpec660.app.shared.utils

import java.io.File
import java.util.*

object AppConfig {
    private val properties = Properties()
    private val resourcesPath = System.getProperty("compose.application.resources.dir")
    private val propertiesFile = File(resourcesPath).resolve("app.properties")

    private val placeholderRegex = Regex("\\$\\{([^}]+)\\}")

    init {
        propertiesFile.inputStream().use {
            properties.load(it)
        }
    }

    fun getProperty(name: String): String? {
        val property = properties.getProperty(name) ?: return null

        if (property.isBlank()) {
            return null
        }

        val placeholders = placeholderRegex.findAll(property)

        if (placeholders.any()) {
            return replacePlaceholders(property, placeholders)
        }

        return property
    }

    private fun replacePlaceholders(property: String, placeholders: Sequence<MatchResult>): String {
        var value = property

        placeholders.forEach { placeholder ->
            val propertyName = placeholder.value.substring(2, placeholder.value.length - 1)
            var propertyValue = System.getProperty(propertyName)

            if (propertyValue == null) {
                propertyValue = properties.getProperty(propertyName)
            }

            value = value.replace(placeholder.value, propertyValue)
        }

        return value
    }

    fun setProperty(name: String, value: String) {
        propertiesFile.outputStream().use {
            properties.setProperty(name, value)
            properties.store(it, null)
        }
    }
}
