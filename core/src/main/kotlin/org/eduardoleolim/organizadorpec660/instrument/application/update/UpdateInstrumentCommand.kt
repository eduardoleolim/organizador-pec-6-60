package org.eduardoleolim.organizadorpec660.instrument.application.update

import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command

class UpdateInstrumentCommand(
    instrumentId: String,
    private val statisticYear: Int,
    private val statisticMonth: Int,
    agencyId: String,
    statisticTypeId: String,
    municipalityId: String,
    private val file: ByteArray
) : Command<InstrumentError, Unit> {
    private val instrumentId = instrumentId.trim()
    private val agencyId = agencyId.trim()
    private val statisticTypeId = statisticTypeId.trim()
    private val municipalityId = municipalityId.trim()

    fun instrumentId(): String {
        return instrumentId
    }

    fun statisticYear(): Int {
        return statisticYear
    }

    fun statisticMonth(): Int {
        return statisticMonth
    }

    fun agencyId(): String {
        return agencyId
    }

    fun statisticTypeId(): String {
        return statisticTypeId
    }

    fun municipalityId(): String {
        return municipalityId
    }

    fun file(): ByteArray {
        return file
    }
}
