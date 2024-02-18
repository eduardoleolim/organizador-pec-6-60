package org.eduardoleolim.organizadorPec660.core.statisticType.application.update

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command

class UpdateStatisticTypeCommand(
    statisticTypeId: String,
    keyCode: String,
    name: String,
    instrumentTypeIds: List<String>
) : Command {
    private val statisticTypeId: String = statisticTypeId.trim()
    private val keyCode: String = keyCode.trim().uppercase()
    private val name: String = name.trim().uppercase()
    private val instrumentTypeIds: List<String> = instrumentTypeIds.distinct().map { it.trim() }

    fun statisticTypeId(): String {
        return statisticTypeId
    }

    fun keyCode(): String {
        return keyCode
    }

    fun name(): String {
        return name
    }

    fun instrumentTypeIds(): List<String> {
        return instrumentTypeIds
    }
}
