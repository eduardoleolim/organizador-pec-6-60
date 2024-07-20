package org.eduardoleolim.organizadorpec660.core.instrument.domain

import java.util.*

sealed class InvalidInstrumentException(override val message: String, override val cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

class InvalidInstrumentIdError(val id: String, override val cause: Throwable?) :
    InvalidInstrumentException("The id <$id> is not a valid instrument id", cause)

class InvalidInstrumentStatisticYearError(year: Int) :
    InvalidInstrumentException("The year <$year> is not a valid instrument statistic year")

class InvalidInstrumentStatisticMonthError(month: Int) :
    InvalidInstrumentException("The month <$month> is not a valid instrument statistic month. It must be between 1 and 12")

class InvalidInstrumentConsecutiveError(consecutive: String) :
    InvalidInstrumentException("The consecutive <$consecutive> is not a valid instrument consecutive")

class InvalidInstrumentFileIdError(id: String, override val cause: Throwable?) :
    InvalidInstrumentException("The id <$id> is not a valid instrument file id", cause)

class InvalidInstrumentUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InvalidInstrumentException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

class InvalidEmptyInstrumentContentError :
    InvalidInstrumentException("The instrument content cannot be empty")

class InvalidInstrumentContentError :
    InvalidInstrumentException("The instrument content is not a valid PDF file")
