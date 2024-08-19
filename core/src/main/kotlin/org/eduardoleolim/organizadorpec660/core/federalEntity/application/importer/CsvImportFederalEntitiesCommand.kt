package org.eduardoleolim.organizadorpec660.core.federalEntity.application.importer

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.CsvFederalEntityImportInput
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

open class CsvImportFederalEntitiesCommand<I : CsvFederalEntityImportInput>(
    private val input: I,
    private val overrideIfExists: Boolean
) : Command<FederalEntityError, List<FederalEntityImportWarning>> {
    fun input(): I {
        return input
    }

    fun overrideIfExists(): Boolean {
        return overrideIfExists
    }
}
