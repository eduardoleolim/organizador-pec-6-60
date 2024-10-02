package org.eduardoleolim.organizadorpec660.instrument.domain

sealed class InstrumentError(override val message: String, override val cause: Throwable? = null) :
    Error(message, cause)

class InstrumentNotFoundError(key: String) : InstrumentError("The instrument with identifier <$key> was not found")

class InstrumentFileNotFoundError(key: String) :
    InstrumentError("The instrument file with identifier <$key> was not found")

class AgencyNotFoundError(val key: String) : InstrumentError("The agency with identifier <$key> was not found")

class StatisticTypeNotFoundError(val key: String) :
    InstrumentError("The statistic type with identifier <$key> was not found")

class MunicipalityNotFoundError(val key: String) :
    InstrumentError("The municipality with identifier <$key> was not found")

class FederalEntityNotFoundError(val key: String) :
    InstrumentError("The federal entity with identifier <$key> was not found")

class InstrumentFileRequiredError : InstrumentError("A new instrument must be saved with a instrument file")

class InstrumentFileFailSaveError(override val cause: Throwable? = null) :
    InstrumentError("An error occurred during saving process")

class InstrumentAlreadyExistsError(
    statisticYear: Int,
    statisticMonth: Int,
    agencyId: String,
    statisticTypeId: String,
    municipalityId: String
) : InstrumentError("The instrument with statistic year <$statisticYear>, statistic month <$statisticMonth>, agency id <$agencyId>, statistic type id <$statisticTypeId>, and municipality id <$municipalityId> already exists")

class CanNotDeleteSavedInstrumentError :
    InstrumentError("The instrument can not be deleted because its status is saved in SIRESO")

class CanNotImportInstrumentsError : InstrumentError("The instruments can not be imported")

class InstrumentImportFieldNotFound(val instrumentName: String, val field: InstrumentImportDataFields) :
    InstrumentError("The instrument <$instrumentName> has a missing field: ${field.value}")
