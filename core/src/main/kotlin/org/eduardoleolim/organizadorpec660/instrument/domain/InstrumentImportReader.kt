package org.eduardoleolim.organizadorpec660.instrument.domain

import arrow.core.Either
import java.util.*

class InstrumentImportData(
    private val statisticYear: Int,
    private val statisticMonth: Int,
    federalEntityKeyCode: String,
    municipalityKeyCode: String,
    agencyConsecutive: String,
    statisticTypeKeyCode: String,
    private val saved: Boolean,
    private val createdAt: Date,
    private val instrumentFileContent: ByteArray
) {
    private val federalEntityKeyCode: String = federalEntityKeyCode.trim()
    private val municipalityKeyCode: String = municipalityKeyCode.trim()
    private val agencyConsecutive: String = agencyConsecutive.trim()
    private val statisticTypeKeyCode: String = statisticTypeKeyCode.trim()

    fun statisticYear(): Int {
        return statisticYear
    }

    fun statisticMonth(): Int {
        return statisticMonth
    }

    fun federalEntityKeyCode(): String {
        return federalEntityKeyCode
    }

    fun municipalityKeyCode(): String {
        return municipalityKeyCode
    }

    fun agencyConsecutive(): String {
        return agencyConsecutive
    }

    fun statisticTypeKeyCode(): String {
        return statisticTypeKeyCode
    }

    fun createdAt(): Date {
        return createdAt
    }

    fun saved(): Boolean {
        return saved
    }

    fun instrumentFileContent(): ByteArray {
        return instrumentFileContent
    }
}

enum class InstrumentImportDataFields(val value: String) {
    STATISTIC_YEAR("Statistic Year"),
    STATISTIC_MONTH("Statistic Month"),
    FEDERAL_ENTITY_KEY_CODE("Federal Entity KeyCode"),
    MUNICIPALITY_KEY_CODE("Municipality KeyCode"),
    AGENCY_CONSECUTIVE("Agency Consecutive"),
    STATISTIC_TYPE_KEY_CODE("Statistic Type KeyCode"),
    SAVED_IN_SIRESO("Saved in SIRESO"),
    CREATED_AT("Created At"),
    INSTRUMENT_FILE_CONTENT("Instrument File Content")
}

abstract class InstrumentImportInput

abstract class AccdbInstrumentImportInput : InstrumentImportInput()

interface InstrumentImportReader<I : InstrumentImportInput> {
    fun read(input: I): List<Either<InstrumentImportFieldNotFound, InstrumentImportData>>
}
