package org.eduardoleolim.organizadorPec660.core.statisticType.application.update

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler

class UpdateStatisticTypeCommandHandler(private val updater: StatisticTypeUpdater) :
    CommandHandler<UpdateStatisticTypeCommand> {
    override fun handle(command: UpdateStatisticTypeCommand) {
        updater.update(command.statisticTypeId(), command.keyCode(), command.name())
    }
}
