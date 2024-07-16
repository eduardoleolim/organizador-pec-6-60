package org.eduardoleolim.organizadorpec660.core.instrument.domain

sealed class InstrumentError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InstrumentNotFoundError(id: String) :
    InstrumentError("The instrument with id <$id> was not found")

class InstrumentFileRequiredError: InstrumentError("A new instrument must be saved with a intrument file")

class InstrumentFileFailSaveError: InstrumentError("An error occurred during saving process")

class InstrumentAlreadyExistsError(
    statisticYear: Int,
    statisticMonth: Int,
    consecutive: String,
    statisticTypeId: String,
    municipalityId: String
) : InstrumentError("The instrument with statistic year <$statisticYear>, statistic month <$statisticMonth>, consecutive <$consecutive>, statistic type id <$statisticTypeId>, and municipality id <$municipalityId> already exists")
