package org.eduardoleolim.organizadorpec660.federalEntity.application.importer

import org.eduardoleolim.organizadorpec660.federalEntity.domain.CsvFederalEntityImportInput
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command

class CsvImportFederalEntitiesCommand<I : CsvFederalEntityImportInput>(
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
