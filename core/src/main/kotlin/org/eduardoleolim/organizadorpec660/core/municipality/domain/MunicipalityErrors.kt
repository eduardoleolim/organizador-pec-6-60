package org.eduardoleolim.organizadorpec660.core.municipality.domain

sealed class MunicipalityError(override val message: String, override val cause: Throwable? = null) :
    Error(message, cause)

class MunicipalityAlreadyExistsError(val keyCode: String) :
    MunicipalityError("The municipality with key code <$keyCode> already exists")

class MunicipalityNotFoundError(val id: String) :
    MunicipalityError("The municipality with id <$id> was not found")

class FederalEntityNotFoundError(val id: String) : MunicipalityError("The federal entity with id <$id> was not found")

class CanNotSaveMunicipalityError(cause: Throwable?) : MunicipalityError("The municipality could not be saved", cause)

class CanNotDeleteMunicipalityError(cause: Throwable?) : MunicipalityError("The municipality could not be saved", cause)
