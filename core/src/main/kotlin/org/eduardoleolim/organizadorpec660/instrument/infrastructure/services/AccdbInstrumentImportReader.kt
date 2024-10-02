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

                val savedInSireso = row.getBoolean("guardado") ?: return@map Either.Left(
                    InstrumentImportFieldNotFound(instrumentName, InstrumentImportDataFields.SAVED_IN_SIRESO)
                )

                val statisticYear = row.getString("añoEstadistico")?.toInt() ?: return@map Either.Left(
                    InstrumentImportFieldNotFound(instrumentName, InstrumentImportDataFields.STATISTIC_YEAR)
                )

                val consecutive = row.getString("consecutivo")?.padStart(4, '0') ?: return@map Either.Left(
                    InstrumentImportFieldNotFound(instrumentName, InstrumentImportDataFields.AGENCY_CONSECUTIVE)
                )

                val statisticMonth = row.getString("mesEstadistico") ?: return@map Either.Left(
                    InstrumentImportFieldNotFound(instrumentName, InstrumentImportDataFields.STATISTIC_MONTH)
                )


                val statisticMonthNumber = getMonthNumber(statisticMonth, Locale.of("es")) ?: return@map Either.Left(
                    InstrumentImportFieldNotFound(instrumentName, InstrumentImportDataFields.STATISTIC_MONTH)
                )

                val municipalityKeyCode = row["folioMunicipio"]?.toString() ?: return@map Either.Left(
                    InstrumentImportFieldNotFound(instrumentName, InstrumentImportDataFields.MUNICIPALITY_KEY_CODE)
                )

                val federalEntityKeyCode = federalEntityKeyCodes.firstOrNull { it.first == municipalityKeyCode }?.second
                    ?: return@map Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.FEDERAL_ENTITY_KEY_CODE
                        )
                    )

                val statisticTypeKeyCode = row["folioTipoEstadistica"]?.toString() ?: return@map Either.Left(
                    InstrumentImportFieldNotFound(instrumentName, InstrumentImportDataFields.STATISTIC_TYPE_KEY_CODE)
                )


                val createdAt = row["fechaRegistro"] as? LocalDateTime ?: return@map Either.Left(
                    InstrumentImportFieldNotFound(instrumentName, InstrumentImportDataFields.CREATED_AT)
                )

                val instrumentFile = row["rutaArchivo"]?.toString()?.replace("\\data\\", "\\")?.let {
                    input.databaseDirectory.parent.resolve(it.replace("\\", File.separator)).normalize().toFile()
                }

                if (instrumentFile == null) {
                    return@map Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.INSTRUMENT_FILE_CONTENT
                        )
                    )
                }

                if (!instrumentFile.exists()) {
                    return@map Either.Left(
                        InstrumentImportFieldNotFound(
                            instrumentName,
                            InstrumentImportDataFields.INSTRUMENT_FILE_CONTENT
                        )
                    )
                }


                Either.Right(
                    InstrumentImportData(
                        statisticYear,
                        statisticMonthNumber,
                        federalEntityKeyCode,
                        municipalityKeyCode,
                        consecutive,
                        statisticTypeKeyCode,
                        savedInSireso,
                        createdAt.toDate(),
                        instrumentFile.readBytes()
                    )
                )
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

