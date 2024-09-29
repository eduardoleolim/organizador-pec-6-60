package org.eduardoleolim.organizadorpec660.statisticType.application.update

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.statisticType.domain.*

class StatisticTypeUpdater(private val statisticTypeRepository: StatisticTypeRepository) {
    fun update(id: String, keyCode: String, name: String): Either<StatisticTypeError, Unit> {
        try {
            val statisticType = searchStatisticType(id) ?: return Either.Left(StatisticTypeNotFoundError(id))

            if (existsAnotherSameKeyCode(id, keyCode))
                return Either.Left(StatisticTypeAlreadyExistsError(keyCode))

            statisticType.apply {
                changeKeyCode(keyCode)
                changeName(name)
            }.let {
                statisticTypeRepository.save(it)
                return Either.Right(Unit)
            }
        } catch (e: InvalidArgumentStatisticTypeException) {
            return Either.Left(CanNotSaveStatisticTypeError(e))
        }
    }

    private fun searchStatisticType(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        statisticTypeRepository.matching(it).firstOrNull()
    }

    private fun existsAnotherSameKeyCode(id: String, keyCode: String) =
        StatisticTypeCriteria.anotherSameKeyCodeCriteria(id, keyCode).let {
            statisticTypeRepository.count(it) > 0
        }
}
