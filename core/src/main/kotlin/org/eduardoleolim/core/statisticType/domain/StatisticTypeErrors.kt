package org.eduardoleolim.core.statisticType.domain

import java.util.*

sealed class StatisticTypeError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidStatisticTypeIdError(val id: String, override val cause: Throwable?) :
    StatisticTypeError("The id <$id> is not a valid statistic type id", cause)

class InvalidStatisticTypeKeyCodeError(val keyCode: String) :
    StatisticTypeError("The key code <$keyCode> is not a valid statistic type key code")

class InvalidStatisticTypeNameError(val name: String) :
    StatisticTypeError("The name <$name> is not a valid statistic type name")

class InvalidStatisticTypeUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    StatisticTypeError("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

class StatisticTypeNotFoundError(val id: String) :
    StatisticTypeError("The statistic type with id <$id> was not found")

class StatisticTypeAlreadyExistsError(val keyCode: String) :
    StatisticTypeError("The statistic type with key code <$keyCode> already exists")

class NotEnoughInstrumentTypesError : StatisticTypeError("The statistic type must have at least one instrument type")
