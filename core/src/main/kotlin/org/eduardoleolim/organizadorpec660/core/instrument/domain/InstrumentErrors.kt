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

class InvalidEmptyInstrumentContentError :
    InstrumentError("The instrument content cannot be empty")

class InvalidInstrumentContentError :
    InstrumentError("The instrument content is not a valid PDF file")

class InstrumentNotFoundError(id: String) :
    InstrumentError("The instrument with id <$id> was not found")

class InstrumentAlreadyExistsError(
    statisticYear: Int,
    statisticMonth: Int,
    consecutive: String,
    instrumentTypeId: String,
    statisticTypeId: String,
    municipalityId: String
) : InstrumentError("The instrument with statistic year <$statisticYear>, statistic month <$statisticMonth>, consecutive <$consecutive>, instrument type id <$instrumentTypeId>, statistic type id <$statisticTypeId>, and municipality id <$municipalityId> already exists")
