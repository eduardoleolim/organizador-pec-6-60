package org.eduardoleolim.organizadorPec660.core.statisticType.application.create

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler

class CreateStatisticTypeCommandHandler(private val creator: StatisticTypeCreator) :
    CommandHandler<CreateStatisticTypeCommand> {
    override fun handle(command: CreateStatisticTypeCommand) {
        creator.create(command.keyCode(), command.name())
    }
}
