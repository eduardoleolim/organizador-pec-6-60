package org.eduardoleolim.organizadorpec660.core.statisticType.application.update

import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeError

class UpdateStatisticTypeCommandHandler(private val updater: StatisticTypeUpdater) :
    CommandHandler<StatisticTypeError, Unit, UpdateStatisticTypeCommand> {
    override fun handle(command: UpdateStatisticTypeCommand): Either<StatisticTypeError, Unit> {
        return updater.update(command.statisticTypeId(), command.keyCode(), command.name())
    }
}
