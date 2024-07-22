package org.eduardoleolim.organizadorpec660.core.instrument.domain

sealed class InstrumentError(override val message: String, override val cause: Throwable? = null) :
    Error(message, cause)

class InstrumentNotFoundError(id: String) : InstrumentError("The instrument with id <$id> was not found")

class AgencyNotFoundError(val id: String) : InstrumentError("The agency with id <$id> was not found")

class StatisticTypeNotFoundError(val id: String) : InstrumentError("The statistic type with id <$id> was not found")

class MunicipalityNotFoundError(val id: String) : InstrumentError("The municipality with id <$id> was not found")

class InstrumentFileRequiredError : InstrumentError("A new instrument must be saved with a intrument file")

class InstrumentFileFailSaveError(override val cause: Throwable? = null) :
    InstrumentError("An error occurred during saving process")

class InstrumentAlreadyExistsError(
    statisticYear: Int,
    statisticMonth: Int,
    agencyId: String,
    statisticTypeId: String,
    municipalityId: String
) : InstrumentError("The instrument with statistic year <$statisticYear>, statistic month <$statisticMonth>, agency id <$agencyId>, statistic type id <$statisticTypeId>, and municipality id <$municipalityId> already exists")
