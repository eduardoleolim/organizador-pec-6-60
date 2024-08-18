package org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.bus

import org.eduardoleolim.organizadorpec660.core.federalEntity.application.importer.ImportFederalEntitiesCommand
import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.services.CsvFederalEntityImportInput

class ImportFederalEntitiesFromCsvCommand(input: CsvFederalEntityImportInput, overrideIfExists: Boolean) :
    ImportFederalEntitiesCommand<CsvFederalEntityImportInput>(input, overrideIfExists)
