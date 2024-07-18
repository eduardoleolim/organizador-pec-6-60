package org.eduardoleolim.organizadorpec660.core.instrument.application.create

import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import java.util.*

class CreateInstrumentCommand(
    private val statisticYear: Int,
    private val statisticMonth: Int,
    agencyId: String,
    statisticTypeId: String,
    municipalityId: String,
    private val file: ByteArray
) : Command<InstrumentError, UUID> {
    private val agencyId = agencyId.trim()
    private val statisticTypeId = statisticTypeId.trim()
    private val municipalityId = municipalityId.trim()

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
