package org.eduardoleolim.organizadorpec660.core.agency.domain

import java.util.*

sealed class AgencyError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidAgencyIdError(val id: String, override val cause: Throwable?) :
    AgencyError("The id <$id> is not a valid agency id", cause)

class InvalidAgencyNameError(val name: String) : AgencyError("The name <$name> is not a valid agency name")

class InvalidAgencyConsecutiveError(val consecutive: String) :
    AgencyError("The consecutive <$consecutive> is not a valid agency consecutive")

class InvalidAgencyMunicipalitiesError : AgencyError("The agency must to have at least a municipality association")

class InvalidAgencyMunicipalityOwnerError : AgencyError("The agency must belong just to a municipality")

class InvalidAgencyStatisticTypesError : AgencyError("The agency must to have at least a statistic type association")

class InvalidAgencyUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    AgencyError("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

class AgencyAlreadyExists(consecutive: String) : AgencyError("The agency with consecutive <$consecutive> already exists")

class AgencyNotFoundError(val id: String) : AgencyError("The agency with id <$id> was not found")
