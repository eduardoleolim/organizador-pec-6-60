package org.eduardoleolim.organizadorpec660.statisticType.application.create

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeError
import java.util.*

class CreateStatisticTypeCommandHandler(private val creator: StatisticTypeCreator) :
    CommandHandler<StatisticTypeError, UUID, CreateStatisticTypeCommand> {
    override fun handle(command: CreateStatisticTypeCommand): Either<StatisticTypeError, UUID> {
        return creator.create(command.keyCode(), command.name())
    }
}
