package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models

import org.ktorm.database.Database
import org.ktorm.support.sqlite.SQLiteDialect
import org.sqlite.mc.SQLiteMCSqlCipherConfig
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.concurrent.thread

object SqliteKtormDatabase {
    private val classLoader = Thread.currentThread().contextClassLoader

    fun exists(databasePath: String): Boolean {
        return File(databasePath).let {
            it.exists() && it.isFile
        }
    }

    fun connect(
        databasePath: String,
        password: String,
        extensions: List<String> = emptyList()
    ) = connect(databasePath, password, false, extensions)

    fun connectReadOnly(
        databasePath: String,
        password: String,
        extensions: List<String> = emptyList()
    ) = connect(databasePath, password, true, extensions)

    private fun connect(
        databasePath: String,
        password: String,
        isReadOnly: Boolean = false,
        extensions: List<String> = emptyList()
    ): Database {
        val file = File(databasePath)
        val existsDatabase = file.exists()

        if (existsDatabase.not())
            file.parentFile.mkdirs()

        val url = "jdbc:sqlite:$databasePath"
        val config = SQLiteMCSqlCipherConfig.getDefault().withKey(password).build().apply {
            enforceForeignKeys(true)
            setReadOnly(isReadOnly)
            enableLoadExtension(true)
        }

        if (existsDatabase.not()) {
            try {
                config.setReadOnly(false)

                DriverManager.getConnection(url, config.toProperties()).use { connection ->
                    connection.createDatabase()
                }

                config.setReadOnly(isReadOnly)
            } catch (e: Exception) {
                file.delete()
                throw e
            }
        }


        val connection = DriverManager.getConnection(url, config.toProperties())
        connection.loadExtensions(extensions)

        Runtime.getRuntime().addShutdownHook(
            thread(start = false) {
                connection.close()
            }
        )

        return Database.connect(SQLiteDialect()) {
            object : Connection by connection {
                override fun close() {
                    // Override the close function and do nothing, keep the connection open.
                }
            }
        }
    }

    private fun Connection.createDatabase() {
        val schemaStream = classLoader.getResourceAsStream("database/schema.sql")
            ?: throw Exception("Database schema not found")

        val schema = schemaStream.bufferedReader().use { it.readText() }
        val queries = schema.split(";").map { it.trim() }.filter { it.isNotEmpty() }

        autoCommit = false

        createStatement().use { statement ->
            queries.forEach { statement.addBatch(it) }
            statement.executeBatch()
        }

        val adminRoleId = UUID.randomUUID().toString()
        val adminUserId = UUID.randomUUID().toString()

        prepareStatement("INSERT INTO role (roleId, name) VALUES (?, ?)").use { statement ->
            statement.setString(1, adminRoleId)
            statement.setString(2, "ADMINISTRADOR")
            statement.execute()
        }

        prepareStatement("INSERT INTO user (userId, firstname, lastname, roleId, createdAt) VALUES (?, ?, ?, ?, ?)").use { statement ->
            statement.setString(1, adminUserId)
            statement.setString(2, "Administrador")
            statement.setString(3, "Administrador")
            statement.setString(4, adminRoleId)
            statement.setObject(5, Date().time)
            statement.execute()
        }

        prepareStatement("INSERT INTO credentials (email, username, password, userId) VALUES (?, ?, ?, ?)").use { statement ->
            statement.setString(1, "admin@localhost")
            statement.setString(2, "admin")
            statement.setString(3, "admin")
            statement.setString(4, adminUserId)
            statement.execute()
        }

        commit()
    }
}

fun Connection.loadExtensions(extensions: List<String>) {
    createStatement().use { statement ->
        extensions.forEach { path ->
            try {
                statement.execute("SELECT load_extension('$path')")
            } catch (e: Exception) {
                println("Error loading extension: $path")
            }
        }
    }
}
