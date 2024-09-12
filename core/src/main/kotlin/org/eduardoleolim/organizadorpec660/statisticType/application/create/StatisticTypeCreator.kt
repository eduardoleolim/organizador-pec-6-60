package org.eduardoleolim.organizadorpec660.statisticType.application.create

import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right
import org.eduardoleolim.organizadorpec660.statisticType.domain.*
import java.util.*

class StatisticTypeCreator(private val statisticTypeRepository: StatisticTypeRepository) {
    fun create(keyCode: String, name: String): Either<StatisticTypeError, UUID> {
        try {
            if (existsStatisticType(keyCode))
                return Left(StatisticTypeAlreadyExistsError(keyCode))

            StatisticType.create(keyCode, name).let {
                statisticTypeRepository.save(it)
                return Right(it.id())
            }
        } catch (e: InvalidArgumentStatisticTypeException) {
            return Left(CanNotSaveStatisticTypeError(e))
        }
    }

    private fun existsStatisticType(keyCode: String) = StatisticTypeCriteria.keyCodeCriteria(keyCode).let {
        statisticTypeRepository.count(it) > 0
    }
}
