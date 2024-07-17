package org.eduardoleolim.organizadorpec660.core.statisticType.domain

sealed class StatisticTypeError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class StatisticTypeNotFoundError(val id: String) :
    StatisticTypeError("The statistic type with id <$id> was not found")

class StatisticTypeUsedInAgency : StatisticTypeError("The statistic type is used by an agency")

class StatisticTypeAlreadyExistsError(val keyCode: String) :
    StatisticTypeError("The statistic type with key code <$keyCode> already exists")

class CanNotSaveStatisticTypeError(cause: Throwable?) :
    StatisticTypeError("The statistic type could not be saved", cause)

class CanNotDeleteStatisticTypeError(cause: Throwable?) :
    StatisticTypeError("The statistic type could not be saved", cause)
