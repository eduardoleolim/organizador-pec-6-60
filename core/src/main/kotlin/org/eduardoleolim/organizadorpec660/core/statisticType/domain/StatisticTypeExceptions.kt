package org.eduardoleolim.organizadorpec660.core.statisticType.domain

import java.util.*

sealed class InvalidArgumentStatisticTypeException(
    override val message: String,
    override val cause: Throwable? = null
) :
    IllegalArgumentException(message, cause)

class InvalidStatisticTypeIdError(val id: String, override val cause: Throwable?) :
    InvalidArgumentStatisticTypeException("The id <$id> is not a valid statistic type id", cause)

class InvalidStatisticTypeKeyCodeError(val keyCode: String) :
    InvalidArgumentStatisticTypeException("The key code <$keyCode> is not a valid statistic type key code")

class InvalidStatisticTypeNameError(val name: String) :
    InvalidArgumentStatisticTypeException("The name <$name> is not a valid statistic type name")

class InvalidStatisticTypeUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InvalidArgumentStatisticTypeException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

