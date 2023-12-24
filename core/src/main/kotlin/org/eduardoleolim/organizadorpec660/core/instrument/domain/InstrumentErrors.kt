package org.eduardoleolim.organizadorpec660.core.instrument.domain

import java.util.*

sealed class InstrumentError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidInstrumentIdError(val id: String, override val cause: Throwable?) :
    InstrumentError("The id <$id> is not a valid instrument id", cause)

class InvalidInstrumentStatisticYearError(year: Int) :
    InstrumentError("The year <$year> is not a valid instrument statistic year")

class InvalidInstrumentStatisticMonthError(month: Int) :
    InstrumentError("The month <$month> is not a valid instrument statistic month")

class InvalidInstrumentConsecutiveError(consecutive: String) :
    InstrumentError("The consecutive <$consecutive> is not a valid instrument consecutive")

class InvalidInstrumentFileIdError(id: String, override val cause: Throwable?) :
    InstrumentError("The id <$id> is not a valid instrument file id", cause)

class InvalidInstrumentUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InstrumentError("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")