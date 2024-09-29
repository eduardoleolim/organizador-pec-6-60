package org.eduardoleolim.organizadorpec660.statisticType.application.create

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.statisticType.domain.*
import java.util.*

class StatisticTypeCreator(private val statisticTypeRepository: StatisticTypeRepository) {
    fun create(keyCode: String, name: String): Either<StatisticTypeError, UUID> {
        try {
            if (existsStatisticType(keyCode))
                return Either.Left(StatisticTypeAlreadyExistsError(keyCode))

            StatisticType.create(keyCode, name).let {
                statisticTypeRepository.save(it)
                return Either.Right(it.id())
            }
        } catch (e: InvalidArgumentStatisticTypeException) {
            return Either.Left(CanNotSaveStatisticTypeError(e))
        }
    }

    private fun existsStatisticType(keyCode: String) = StatisticTypeCriteria.keyCodeCriteria(keyCode).let {
        statisticTypeRepository.count(it) > 0
    }
}
