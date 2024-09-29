package org.eduardoleolim.organizadorpec660.statisticType.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeError

class DeleteStatisticTypeCommandHandler(private val deleter: StatisticTypeDeleter) :
    CommandHandler<StatisticTypeError, Unit, DeleteStatisticTypeCommand> {
    override fun handle(command: DeleteStatisticTypeCommand): Either<StatisticTypeError, Unit> {
        return deleter.delete(command.statisticTypeId())
    }
}
