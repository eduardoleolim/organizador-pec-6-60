package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.database.Database
import org.ktorm.support.sqlite.SQLiteDialect
import org.sqlite.SQLiteDataSource
import java.io.File


object SqliteKtormDatabase {
    fun connect(databasePath: String, isReadOnly: Boolean = false): Database {
        val file = File(databasePath)
        val existsFile = file.exists()

        if (!existsFile)
            file.parentFile.mkdirs()

        SQLiteDataSource().apply {
            url = "jdbc:sqlite:$databasePath"
            setEnforceForeignKeys(true)
            setReadOnly(isReadOnly)
            return Database.connect(this, SQLiteDialect()).apply {
                if (existsFile)
                    return this

                try {
                    createDatabase(this)
                } catch (e: Exception) {
                    file.delete()
                    throw e
                }
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
        }
    }
}
