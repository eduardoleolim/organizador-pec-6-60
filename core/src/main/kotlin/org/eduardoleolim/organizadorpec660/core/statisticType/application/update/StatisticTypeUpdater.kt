package org.eduardoleolim.organizadorpec660.core.statisticType.application.update

import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.*

class StatisticTypeUpdater(private val statisticTypeRepository: StatisticTypeRepository) {
    fun update(id: String, keyCode: String, name: String): Either<StatisticTypeError, Unit> {
        try {
            val statisticType = searchStatisticType(id) ?: return Left(StatisticTypeNotFoundError(id))

            if (existsAnotherSameKeyCode(id, keyCode))
                return Left(StatisticTypeAlreadyExistsError(keyCode))

            statisticType.apply {
                changeKeyCode(keyCode)
                changeName(name)
            }.let {
                statisticTypeRepository.save(it)
                return Right(Unit)
            }
        } catch (e: InvalidArgumentStatisticTypeException) {
            return Left(CanNotSaveStatisticTypeError(e))
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
