package org.eduardoleolim.organizadorpec660.core.agency.domain

import java.util.*

sealed class InvalidArgumentAgencyException(override val message: String, override val cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

class InvalidAgencyIdException(val id: String, override val cause: Throwable?) :
    InvalidArgumentAgencyException("The id <$id> is not a valid agency id", cause)

class InvalidAgencyNameException(val name: String) :
    InvalidArgumentAgencyException("The name <$name> is not a valid agency name")

class InvalidAgencyConsecutiveException(val consecutive: String) :
    InvalidArgumentAgencyException("The consecutive <$consecutive> is not a valid agency consecutive")

class InvalidAgencyStatisticTypesException :
    InvalidArgumentAgencyException("The agency must to have at least a statistic type association")

class InvalidAgencyUpdateDateException(val updatedAt: Date, val createdAt: Date) :
    InvalidArgumentAgencyException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

