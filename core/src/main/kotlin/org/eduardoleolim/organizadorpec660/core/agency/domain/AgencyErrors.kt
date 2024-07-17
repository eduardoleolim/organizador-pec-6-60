package org.eduardoleolim.organizadorpec660.core.agency.domain

sealed class AgencyError(override val message: String, override val cause: Throwable? = null) : Error(message, cause)

class AgencyAlreadyExistsError(consecutive: String) :
    AgencyError("The agency with consecutive <$consecutive> already exists")

class AgencyNotFoundError(val id: String) : AgencyError("The agency with id <$id> was not found")

class AgencyHasNoStatisticTypesError : AgencyError("The agency must be related with at least one statistic type")

class AgencyHasInstrumentsError : AgencyError("There are instruments related to the agency")

class MunicipalityNotFoundError(val id: String) : AgencyError("The municipality with id <$id> was not found")

class StatisticTypeNotFoundError(val id: String) : AgencyError("The municipality with id <$id> was not found")

class CanNotSaveAgencyError(cause: Throwable?) : AgencyError("The agency could not be saved", cause)

class CanNotDeleteAgencyError(cause: Throwable?) : AgencyError("The agency could not be deleted", cause)
