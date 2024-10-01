package org.eduardoleolim.organizadorpec660.shared.utils

import com.ufoscout.properlty.Default
import com.ufoscout.properlty.Properlty
import com.ufoscout.properlty.reader.SystemPropertiesReader
import net.harawata.appdirs.AppDirsFactory
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

object AppConfig {
    private val appDirs = AppDirsFactory.getInstance()

    private val resourcesDirectory = System.getProperty("compose.application.resources.dir")

    val name: String = System.getProperty("app.name")

    val version: String = System.getProperty("app.version")

    val configDirectory: String
        get() = System.getProperty("app.config.dir") ?: appDirs.getSiteConfigDir(name, null, null)

    private val propertiesFile = File(configDirectory, "app.properties").also {
        if (it.exists().not()) {
            val defaultConfigFile = File(resourcesDirectory).resolve("app.properties")
            try {
                defaultConfigFile.copyTo(it, overwrite = true)
            } catch (e: IOException) {
                throw RuntimeException("Error copying default properties file to config directory", e)
            }
        }
    }

    private var properties = Properlty.builder().add(SystemPropertiesReader()).add(propertiesFile.path).build()

    val dataDirectory: String
        get() = properties["app.data.dir"] ?: appDirs.getSiteDataDir(name, null, null)

    val logsDirectory: String
        get() = properties["app.logs.dir"] ?: appDirs.getUserLogDir(name, null, null)

    operator fun get(key: String): String? = properties[key]

    operator fun set(key: String, value: String) {
        if (key.startsWith("app.")) {
            propertiesFile.let { pptFile ->
                val appProperties = pptFile.inputStream().use { Properties().apply { load(it) } }
                appProperties.setProperty(key, value)
                pptFile.outputStream().use { appProperties.store(it, null) }
            }
        } else {
            System.setProperty(key, value)
        }

        properties = Properlty.builder()
            .add(SystemPropertiesReader())
            .add(propertiesFile.path)
            .build()
    }

    operator fun get(key: String, defaultValue: String): String = properties[key] ?: defaultValue

    operator fun <T> get(key: String, map: (String) -> T): T? = properties[key, map]

    operator fun <T> get(key: String, defaultValue: T, map: (String) -> T): T = properties[key, defaultValue, map]

    fun getBoolean(key: String): Boolean? = properties.getBoolean(key)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean = properties.getBoolean(key, defaultValue)

    fun getInt(key: String): Int? = properties.getInt(key)

    fun getInt(key: String, defaultValue: Int): Int = properties.getInt(key, defaultValue)

    fun getDouble(key: String): Double? = properties.getDouble(key)

    fun getDouble(key: String, defaultValue: Double): Double = properties.getDouble(key, defaultValue)

    fun getFloat(key: String): Float? = properties.getFloat(key)

    fun getFloat(key: String, defaultValue: Float): Float = properties.getFloat(key, defaultValue)

    fun getLong(key: String): Long? = properties.getLong(key)

    fun getLong(key: String, defaultValue: Long): Long = properties.getLong(key, defaultValue)

    fun getBigDecimal(key: String): BigDecimal? = properties.getBigDecimal(key)

    fun getBigDecimal(key: String, defaultValue: BigDecimal): BigDecimal = properties.getBigDecimal(key, defaultValue)

    fun getBigInteger(key: String): BigInteger? = properties.getBigInteger(key)

    fun getBigInteger(key: String, defaultValue: BigInteger): BigInteger = properties.getBigInteger(key, defaultValue)

    fun getArray(key: String, separator: String = Default.LIST_SEPARATOR): Array<String> =
        properties.getArray(key, separator)

    fun getList(key: String, separator: String = Default.LIST_SEPARATOR): List<String> =
        properties.getList(key, separator)

    fun <T> getList(key: String, map: (String) -> T): List<T> = properties.getList(key, map)

    fun <T> getList(key: String, separator: String = Default.LIST_SEPARATOR, map: (String) -> T): List<T> =
        properties.getList(key, separator, map)
}
