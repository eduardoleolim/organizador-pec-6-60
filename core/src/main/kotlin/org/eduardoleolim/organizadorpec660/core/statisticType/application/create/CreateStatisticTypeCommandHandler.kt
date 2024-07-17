package org.eduardoleolim.organizadorpec660.core.statisticType.application.create

import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeError
import java.util.*

class CreateStatisticTypeCommandHandler(private val creator: StatisticTypeCreator) :
    CommandHandler<StatisticTypeError, UUID, CreateStatisticTypeCommand> {
    override fun handle(command: CreateStatisticTypeCommand): Either<StatisticTypeError, UUID> {
        return creator.create(command.keyCode(), command.name())
    }
}
