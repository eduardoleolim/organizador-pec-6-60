package org.eduardoleolim.organizadorpec660.core.agency.domain

sealed class AgencyError(override val message: String, override val cause: Throwable? = null) : Error(message, cause)

class AgencyAlreadyExistsError(consecutive: String) :
    AgencyError("The agency with consecutive <$consecutive> already exists")

class AgencyNotFoundError(val id: String) : AgencyError("The agency with id <$id> was not found")

class AgencyHasInstrumentsError : AgencyError("There are instruments related to the agency")
