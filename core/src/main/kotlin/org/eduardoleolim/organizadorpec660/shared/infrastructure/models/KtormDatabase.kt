/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.shared.infrastructure.models

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.ktorm.database.Database
import org.ktorm.support.sqlite.SQLiteDialect
import org.sqlite.mc.SQLiteMCSqlCipherConfig
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.thread

object SqliteKtormDatabase {
    private val classLoader = Thread.currentThread().contextClassLoader
    private val csvReader = csvReader()

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

                DriverManager.getConnection(url, config.toProperties()).use {
                    it.createDatabase()
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
        val federalEntitiesStream = classLoader.getResourceAsStream("catalogs/FederalEntities.csv")
            ?: throw Exception("Federal Entities catalog not found")
        val municipalitiesStream = classLoader.getResourceAsStream("catalogs/Municipalities.csv")
            ?: throw Exception("Municipalities catalog not found")
        val statisticTypesStream = classLoader.getResourceAsStream("catalogs/StatisticTypes.csv")
            ?: throw Exception("Statistic Types catalog not found")
        val agenciesStream = classLoader.getResourceAsStream("catalogs/Agencies.csv")
            ?: throw Exception("Agencies catalog not found")

        val schema = schemaStream.bufferedReader().use { it.readText() }
        val queries = schema.split(";").map { it.trim() }.filter { it.isNotEmpty() }
        val federalEntities = csvReader.readAllWithHeader(federalEntitiesStream)
        val municipalities = csvReader.readAllWithHeader(municipalitiesStream)
        val statisticTypes = csvReader.readAllWithHeader(statisticTypesStream)
        val agencies = csvReader.readAllWithHeader(agenciesStream)

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

        try {
            val federalEntityIds = mutableListOf<Pair<UUID, String>>()
            val municipalityIds = mutableListOf<Pair<UUID, String>>()
            val statisticTypeIds = mutableListOf<Pair<UUID, String>>()
            val queryFederalEntity =
                "INSERT INTO federalEntity (federalEntityId, keyCode, name, createdAt) VALUES (?, ?, ?, ?)"
            val queryMunicipality =
                "INSERT INTO municipality (municipalityId, keyCode, name, createdAt, federalEntityId) VALUES (?, ?, ?, ?, ?)"
            val queryStatisticType =
                "INSERT INTO statisticType (statisticTypeId, keyCode, name, createdAt) VALUES (?, ?, ?, ?)"
            val queryAgency =
                "INSERT INTO agency (agencyId, name, consecutive, municipalityId, createdAt) VALUES (?, ?, ?, ?, ?)"
            val queryAgencyStatisticType = "INSERT INTO statisticType_agency (agencyId, statisticTypeId) VALUES (?, ?)"

            prepareStatement(queryFederalEntity).use { statement ->
                federalEntities.forEach { row ->
                    val id = UUID.randomUUID()
                    val keyCode = row["KeyCode"]?.padStart(2, '0') ?: ""
                    val name = row["Name"]?.uppercase() ?: ""
                    val createdAt = java.sql.Date.valueOf(LocalDate.now())

                    statement.setString(1, id.toString())
                    statement.setString(2, keyCode)
                    statement.setString(3, name)
                    statement.setDate(4, createdAt)
                    statement.addBatch()

                    federalEntityIds.add(Pair(id, keyCode))
                }

                statement.executeBatch()
            }

            prepareStatement(queryMunicipality).use { statement ->
                municipalities.forEach { row ->
                    val federalEntityKeyCode = row["Federal Entity"]?.padStart(2, '0')
                    federalEntityIds.find { it.second == federalEntityKeyCode }?.first?.let { federalEntityId ->
                        val id = UUID.randomUUID()
                        val keyCode = row["KeyCode"]?.padStart(3, '0') ?: ""
                        val name = row["Name"]?.uppercase() ?: ""
                        val createdAt = java.sql.Date.valueOf(LocalDate.now())

                        statement.setString(1, id.toString())
                        statement.setString(2, keyCode)
                        statement.setString(3, name)
                        statement.setDate(4, createdAt)
                        statement.setString(5, federalEntityId.toString())
                        statement.addBatch()

                        municipalityIds.add(Pair(id, "$federalEntityKeyCode-$keyCode"))
                    }

                    statement.executeBatch()
                }
            }

            prepareStatement(queryStatisticType).use { statement ->
                statisticTypes.forEach { row ->
                    val id = UUID.randomUUID()
                    val keyCode = row["KeyCode"]?.padStart(3, '0') ?: ""
                    val name = row["Name"]?.uppercase() ?: ""
                    val createdAt = java.sql.Date.valueOf(LocalDate.now())

                    statement.setString(1, id.toString())
                    statement.setString(2, keyCode)
                    statement.setString(3, name)
                    statement.setDate(4, createdAt)
                    statement.addBatch()

                    statisticTypeIds.add(Pair(id, keyCode))
                }

                statement.executeBatch()
            }

            prepareStatement(queryAgency).use { agencyStatement ->
                prepareStatement(queryAgencyStatisticType).use { agencyStatisticTypeStatement ->
                    agencies.forEach { row ->
                        val federalEntityKeyCode = row["Federal Entity"]?.padStart(2, '0')
                        val municipalityKeyCode = row["Municipality"]?.padStart(3, '0')

                        municipalityIds.find { it.second == "$federalEntityKeyCode-$municipalityKeyCode" }?.first?.let { municipalityId ->
                            val id = UUID.randomUUID()
                            val consecutive = row["Consecutive"]?.padStart(4, '0') ?: ""
                            val name = row["Name"]?.uppercase() ?: ""
                            val createdAt = java.sql.Date.valueOf(LocalDate.now())
                            val statisticTypesOfAgency =
                                row["Statistic Types"]?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                                    ?.map { it.padStart(3, '0') }
                                    ?: emptyList()

                            agencyStatement.setString(1, id.toString())
                            agencyStatement.setString(2, name)
                            agencyStatement.setString(3, consecutive)
                            agencyStatement.setString(4, municipalityId.toString())
                            agencyStatement.setDate(5, createdAt)
                            agencyStatement.addBatch()

                            statisticTypesOfAgency.forEach { statisticTypeOfAgency ->
                                statisticTypeIds.find { it.second == statisticTypeOfAgency }?.first?.let { statisticTypeId ->
                                    agencyStatisticTypeStatement.setString(1, id.toString())
                                    agencyStatisticTypeStatement.setString(2, statisticTypeId.toString())
                                    agencyStatisticTypeStatement.addBatch()
                                }
                            }
                        }
                    }

                    agencyStatement.executeBatch()
                    agencyStatisticTypeStatement.executeBatch()
                }
            }


            commit()
        } catch (e: Exception) {
            println(e)
            rollback()
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
