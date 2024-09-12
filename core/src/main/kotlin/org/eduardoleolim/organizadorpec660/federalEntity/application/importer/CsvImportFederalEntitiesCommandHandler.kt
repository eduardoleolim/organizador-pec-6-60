package org.eduardoleolim.organizadorpec660.federalEntity.application.importer

import org.eduardoleolim.organizadorpec660.federalEntity.domain.CsvFederalEntityImportInput
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class CsvImportFederalEntitiesCommandHandler<I : CsvFederalEntityImportInput>(private val importer: FederalEntityImporter<I>) :
    CommandHandler<FederalEntityError, List<FederalEntityImportWarning>, CsvImportFederalEntitiesCommand<I>> {
    override fun handle(command: CsvImportFederalEntitiesCommand<I>): Either<FederalEntityError, List<FederalEntityImportWarning>> {
        return importer.import(command.input(), command.overrideIfExists())
    }
}
