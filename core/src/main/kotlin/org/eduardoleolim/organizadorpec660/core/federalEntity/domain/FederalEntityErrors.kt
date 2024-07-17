package org.eduardoleolim.organizadorpec660.core.federalEntity.domain

sealed class FederalEntityError(override val message: String, override val cause: Throwable? = null) :
    Error(message, cause)

class FederalEntityNotFoundError(val id: String) :
    FederalEntityError("The federal entity with id <$id> was not found")

class FederalEntityHasMunicipalitiesError : FederalEntityError("The federal entity has municipalities")

class FederalEntityAlreadyExistsError(val keyCode: String) :
    FederalEntityError("The federal entity with key code <$keyCode> already exists")

class CanNotSaveFederalEntityError(cause: Throwable?) :
    FederalEntityError("The federal entity could not be saved", cause)

class CanNotDeleteFederalEntityError(cause: Throwable?) :
    FederalEntityError("The federal entity could not be saved", cause)
