package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.database.Database
import org.ktorm.support.sqlite.SQLiteDialect
import org.sqlite.SQLiteDataSource
import java.io.File
import java.sql.DriverManager

class SqliteKtormDatabase(private val databasePath: String) {
    init {
        if (!existsDatabase(databasePath)) {
            createDatabase(databasePath)
        }
    }

    fun init(isReadOnly: Boolean = false): Database {
        SQLiteDataSource().apply {
            url = "jdbc:sqlite:$databasePath"
            setEnforceForeignKeys(true)
            setReadOnly(isReadOnly)
            return Database.connect(this, SQLiteDialect())
        }
    }

    private fun existsDatabase(databasePath: String): Boolean {
        val file = File(databasePath)
        return file.exists()
    }

    private fun createDatabase(databasePath: String) {
        File(databasePath).parentFile.mkdirs()

        val classLoader = Thread.currentThread().contextClassLoader
        classLoader.getResourceAsStream("database/schema.sql")?.let { stream ->
            val schema = stream.bufferedReader().readText()
            val connection = DriverManager.getConnection("jdbc:sqlite:$databasePath")
            connection.use { conn ->
                val statements = schema.split(";").dropLastWhile { it.isBlank() }
                val statement = conn.createStatement()

                for (sql in statements) {
                    statement.execute(sql)
                }

                statement.close()
            }
        } ?: throw Exception("Database schema not found")
    }
}
