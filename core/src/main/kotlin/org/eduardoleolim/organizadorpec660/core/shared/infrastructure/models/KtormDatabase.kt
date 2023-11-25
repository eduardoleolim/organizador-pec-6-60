package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.database.Database
import org.ktorm.dsl.insert
import org.ktorm.support.sqlite.SQLiteDialect
import org.sqlite.SQLiteDataSource
import java.io.File
import java.time.LocalDateTime
import java.util.*

object SqliteKtormDatabase {
    fun connect(databasePath: String, isReadOnly: Boolean = false): Database {
        val file = File(databasePath)
        val existsFile = file.exists()

        if (!existsFile)
            file.parentFile.mkdirs()

        val dataSource = SQLiteDataSource().apply {
            url = "jdbc:sqlite:$databasePath"
            setEnforceForeignKeys(true)
            setReadOnly(isReadOnly)
        }

        return Database.connect(dataSource, SQLiteDialect()).apply {
            try {
                if (!existsFile)
                    createDatabase(this)
            } catch (e: Exception) {
                file.delete()
                throw e
            }
        }
    }

    private fun createDatabase(database: Database) {
        val classLoader = Thread.currentThread().contextClassLoader
        val schemaStream = classLoader.getResourceAsStream("database/schema.sql")
            ?: throw Exception("Database schema not found")

        val schema = schemaStream.bufferedReader().use { it.readText() }
        val queries = schema.split(";").map { it.trim() }.filter { it.isNotEmpty() }

        database.useTransaction { transaction ->
            transaction.connection.createStatement().use { statement ->
                queries.forEach { statement.addBatch(it) }
                statement.executeBatch()
            }

            val adminRoleId = UUID.randomUUID().toString()
            val adminUserId = UUID.randomUUID().toString()

            database.insert(Roles()) {
                set(it.id, adminRoleId)
                set(it.name, "ADMINISTRADOR")
            }

            database.insert(Users()) {
                set(it.id, adminUserId)
                set(it.firstname, "Administrador")
                set(it.lastname, "Administrador")
                set(it.roleId, adminRoleId)
                set(it.createdAt, LocalDateTime.now())
            }

            database.insert(Credentials()) {
                set(it.email, "admin@localhost")
                set(it.username, "admin")
                set(it.password, "admin")
                set(it.userId, adminUserId)
            }
        }
    }
}
