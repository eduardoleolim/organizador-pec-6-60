package org.eduardoleolim.organizadorpec660.statisticType.application.update

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeError

class UpdateStatisticTypeCommandHandler(private val updater: StatisticTypeUpdater) :
    CommandHandler<StatisticTypeError, Unit, UpdateStatisticTypeCommand> {
    override fun handle(command: UpdateStatisticTypeCommand): Either<StatisticTypeError, Unit> {
        return updater.update(command.statisticTypeId(), command.keyCode(), command.name())
    }
}
