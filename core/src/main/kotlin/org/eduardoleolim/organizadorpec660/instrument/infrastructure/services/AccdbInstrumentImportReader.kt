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

package org.eduardoleolim.organizadorpec660.instrument.infrastructure.services

import arrow.core.Either
import com.healthmarketscience.jackcess.DatabaseBuilder
import org.eduardoleolim.organizadorpec660.instrument.domain.*
import org.eduardoleolim.organizadorpec660.shared.domain.toDate
import java.io.File
import java.nio.file.Path
import java.text.DateFormatSymbols
import java.time.LocalDateTime
import java.util.*

class V1AccdbInstrumentImportInput(val databaseDirectory: Path) : AccdbInstrumentImportInput()

class FileSystemInstrumentFileContentReader(private val file: File) : InstrumentFileContentReader() {
    override fun read(): ByteArray {
        return file.readBytes()
    }
}

class AccdbInstrumentImportReader : InstrumentImportReader<AccdbInstrumentImportInput> {
    override fun read(input: AccdbInstrumentImportInput): List<Either<InstrumentImportFieldNotFound, InstrumentImportData>> {
        return when (input) {
            is V1AccdbInstrumentImportInput -> readWithJackcess(input)
            else -> error("There's no a reader for this <${input::class}> input")
        }
    }

    private fun readWithJackcess(input: V1AccdbInstrumentImportInput): List<Either<InstrumentImportFieldNotFound, InstrumentImportData>> {
        val database = DatabaseBuilder.open(input.databaseDirectory) ?: error("Can not open the database")

        return database.use { db ->
            val instrumentsTable = db.getTable("PEC_6_60") ?: error("Table PEC_6_60 not found")
            val municipalityTable = db.getTable("Municipio") ?: error("Table Municipio not found")

            val federalEntityKeyCodes = municipalityTable.map { row ->
                Pair(row["folio"]?.toString() ?: "", row["folioEntidad"]?.toString() ?: "")
            }

            instrumentsTable.map { row ->
                val instrumentName = row.getString("nombre")
                val savedInSireso = row.getBoolean("guardado")
                val statisticYear = row.getString("añoEstadistico")?.toInt()
                val consecutive = row.getString("consecutivo")?.padStart(4, '0')
                val statisticMonth = row.getString("mesEstadistico")
                val statisticMonthNumber = getMonthNumber(statisticMonth, Locale.of("es"))
                val municipalityKeyCode = row["folioMunicipio"]?.toString()?.padStart(3, '0')
                val federalEntityKeyCode =
                    federalEntityKeyCodes.firstOrNull { it.first == municipalityKeyCode }?.second?.padStart(2, '0')
                val statisticTypeKeyCode = row["folioTipoEstadistica"]?.toString()?.padStart(3, '0')
                val createdAt = row["fechaRegistro"] as? LocalDateTime
                val instrumentFile = row["rutaArchivo"]?.toString()?.replace("\\data\\", "\\")?.let {
                    input.databaseDirectory.parent.resolve(it.replace("\\", File.separator)).normalize().toFile()
                }

                when {
                    savedInSireso == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.SAVED_IN_SIRESO
                        )
                    )

                    statisticYear == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.STATISTIC_YEAR
                        )
                    )

                    consecutive == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.AGENCY_CONSECUTIVE
                        )
                    )

                    statisticMonth == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.STATISTIC_MONTH
                        )
                    )

                    statisticMonthNumber == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.STATISTIC_MONTH
                        )
                    )

                    municipalityKeyCode == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.MUNICIPALITY_KEY_CODE
                        )
                    )

                    federalEntityKeyCode == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.FEDERAL_ENTITY_KEY_CODE
                        )
                    )

                    statisticTypeKeyCode == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.STATISTIC_TYPE_KEY_CODE
                        )
                    )

                    createdAt == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.CREATED_AT
                        )
                    )

                    instrumentFile == null -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.INSTRUMENT_FILE_CONTENT
                        )
                    )

                    !instrumentFile.exists() -> Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.INSTRUMENT_FILE_LOCATION
                        )
                    )

                    else -> Either.Right(
                        InstrumentImportData(
                            statisticYear,
                            statisticMonthNumber,
                            federalEntityKeyCode,
                            municipalityKeyCode,
                            consecutive,
                            statisticTypeKeyCode,
                            savedInSireso,
                            createdAt.toDate(),
                            FileSystemInstrumentFileContentReader(instrumentFile)
                        )
                    )
                }
            }
        }
    }

    private fun getMonthNumber(month: String, locale: Locale = Locale.getDefault()): Int? {
        val months = DateFormatSymbols(locale).months

        months.forEachIndexed { index, monthName ->
            if (monthName.equals(month, ignoreCase = true)) {
                return index + 1
            }
        }

        val englishMonths = DateFormatSymbols(Locale.ENGLISH).months
        englishMonths.forEachIndexed { index, monthName ->
            if (monthName.equals(month, ignoreCase = true)) {
                return index + 1
            }
        }

        return null
    }
}

