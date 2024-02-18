package org.eduardoleolim.organizadorPec660.core.statisticType.application.delete

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler

class DeleteStatisticTypeCommandHandler(private val deleter: StatisticTypeDeleter) :
    CommandHandler<DeleteStatisticTypeCommand> {
    override fun handle(command: DeleteStatisticTypeCommand) {
        deleter.delete(command.statisticTypeId())
    }
}
