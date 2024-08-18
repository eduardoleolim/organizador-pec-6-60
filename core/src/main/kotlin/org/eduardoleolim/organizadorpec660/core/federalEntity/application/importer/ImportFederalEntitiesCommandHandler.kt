package org.eduardoleolim.organizadorpec660.core.federalEntity.application.importer

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityImportInput
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class ImportFederalEntitiesCommandHandler<I : FederalEntityImportInput>(private val importer: FederalEntityImporter<I>) :
    CommandHandler<FederalEntityError, List<FederalEntityImportWarning>, ImportFederalEntitiesCommand<I>> {
    override fun handle(command: ImportFederalEntitiesCommand<I>): Either<FederalEntityError, List<FederalEntityImportWarning>> {
        return importer.import(command.input(), command.overrideIfExists())
    }
}
