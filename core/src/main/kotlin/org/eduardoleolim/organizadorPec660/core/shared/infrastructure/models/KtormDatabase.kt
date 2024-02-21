package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models

import org.ktorm.database.Database
import org.ktorm.dsl.insert
import org.ktorm.support.sqlite.SQLiteDialect
import org.sqlite.SQLiteDataSource
import org.sqlite.mc.SQLiteMCSqlCipherConfig
import java.io.File
import java.sql.Connection
import java.time.LocalDateTime
import java.util.*


object SqliteKtormDatabase {
    private val classLoader = Thread.currentThread().contextClassLoader

    fun connect(
        databasePath: String,
        isReadOnly: Boolean = false,
        extensions: List<String> = emptyList()
    ): Database {
        val file = File(databasePath)
        val existsFile = file.exists()

        if (!existsFile)
            file.parentFile.mkdirs()

        val dataSource = SQLiteDataSource().apply {
            url = "jdbc:sqlite:$databasePath"
            config = SQLiteMCSqlCipherConfig.getDefault().withKey("12345").build().apply {
                setEnforceForeignKeys(true)
                setReadOnly(if (existsFile) isReadOnly else false)
                enableLoadExtension(true)
            }
        }

        return Database.connect(dataSource, SQLiteDialect()).also {
            it.runCatching {
                if (!existsFile) {
                    it.createDatabase(extensions)
                    dataSource.setReadOnly(isReadOnly)
                }
            }.onFailure { error ->
                file.delete()
                throw error
            }
        }
    }

    private fun Database.createDatabase(extensionPaths: List<String>) {
        val schemaStream = classLoader.getResourceAsStream("database/schema.sql")
            ?: throw Exception("Database schema not found")

        val schema = schemaStream.bufferedReader().use { it.readText() }
        val queries = schema.split(";").map { it.trim() }.filter { it.isNotEmpty() }

        useTransaction { transaction ->
            transaction.apply {
                connection.loadExtensions(extensionPaths)
                connection.createStatement().use { statement ->
                    queries.forEach { statement.addBatch(it) }
                    statement.executeBatch()
                }
            }

            val adminRoleId = UUID.randomUUID().toString()
            val adminUserId = UUID.randomUUID().toString()

            insert(Roles()) {
                set(it.id, adminRoleId)
                set(it.name, "ADMINISTRADOR")
            }

            insert(Users()) {
                set(it.id, adminUserId)
                set(it.firstname, "Administrador")
                set(it.lastname, "Administrador")
                set(it.roleId, adminRoleId)
                set(it.createdAt, LocalDateTime.now())
            }

            insert(Credentials()) {
                set(it.email, "admin@localhost")
                set(it.username, "admin")
                set(it.password, "admin")
                set(it.userId, adminUserId)
            }
        }
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
