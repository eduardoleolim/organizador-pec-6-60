package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin

import org.eduardoleolim.organizadorpec660.core.agency.application.create.AgencyCreator
import org.eduardoleolim.organizadorpec660.core.agency.application.delete.AgencyDeleter
import org.eduardoleolim.organizadorpec660.core.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.core.agency.application.update.AgencyUpdater
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.core.agency.infrastructure.persistence.KtormAgencyRepository
import org.eduardoleolim.organizadorpec660.core.auth.application.authenticate.UserAuthenticator
import org.eduardoleolim.organizadorpec660.core.auth.domain.AuthRepository
import org.eduardoleolim.organizadorpec660.core.auth.infrastructure.persistence.KtormAuthRepository
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.create.FederalEntityCreator
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.delete.FederalEntityDeleter
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.importer.FederalEntityImporter
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.update.FederalEntityUpdater
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.CsvFederalEntityImportInput
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityImportReader
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.services.CsvFederalEntityImportReader
import org.eduardoleolim.organizadorpec660.core.instrument.application.create.InstrumentCreator
import org.eduardoleolim.organizadorpec660.core.instrument.application.delete.InstrumentDeleter
import org.eduardoleolim.organizadorpec660.core.instrument.application.save.InstrumentSiresoSaver
import org.eduardoleolim.organizadorpec660.core.instrument.application.search.InstrumentSearcher
import org.eduardoleolim.organizadorpec660.core.instrument.application.update.InstrumentUpdater
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.core.instrument.infrastructure.persistence.KtormInstrumentRepository
import org.eduardoleolim.organizadorpec660.core.municipality.application.create.MunicipalityCreator
import org.eduardoleolim.organizadorpec660.core.municipality.application.delete.MunicipalityDeleter
import org.eduardoleolim.organizadorpec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.core.municipality.application.update.MunicipalityUpdater
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.persistence.KtormMunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.application.create.StatisticTypeCreator
import org.eduardoleolim.organizadorpec660.core.statisticType.application.delete.StatisticTypeDeleter
import org.eduardoleolim.organizadorpec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.core.statisticType.application.update.StatisticTypeUpdater
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.persistence.KtormStatisticTypeRepository
import org.eduardoleolim.organizadorpec660.core.user.application.search.UserSearcher
import org.eduardoleolim.organizadorpec660.core.user.domain.UserRepository
import org.eduardoleolim.organizadorpec660.core.user.infrastructure.persistence.KtormUserRepository
import org.koin.dsl.module
import org.ktorm.database.Database

object KtormAppModule {
    fun buildModule(database: Database, instrumentsPath: String) = module {
        single<Database> { database }

        // Repositories
        single<AgencyRepository> { KtormAgencyRepository(get()) }
        single<AuthRepository> { KtormAuthRepository(get()) }
        single<FederalEntityRepository> { KtormFederalEntityRepository(get()) }
        single<InstrumentRepository> { KtormInstrumentRepository(get(), instrumentsPath) }
        single<MunicipalityRepository> { KtormMunicipalityRepository(get()) }
        // single<RoleRepository> { KtormRoleRepository(get()) }
        single<StatisticTypeRepository> { KtormStatisticTypeRepository(get()) }
        single<UserRepository> { KtormUserRepository(get()) }

        // Agency services
        single { AgencyCreator(get(), get(), get()) }
        single { AgencyDeleter(get(), get()) }
        single { AgencySearcher(get()) }
        single { AgencyUpdater(get(), get(), get()) }

        // Auth services
        single { UserAuthenticator(get()) }

        // Federal Entity services
        single { FederalEntityCreator(get()) }
        single { FederalEntityDeleter(get(), get()) }
        single { FederalEntitySearcher(get()) }
        single { FederalEntityUpdater(get()) }
        single<FederalEntityImportReader<CsvFederalEntityImportInput>> { CsvFederalEntityImportReader() }
        single<FederalEntityImporter<CsvFederalEntityImportInput>> { FederalEntityImporter(get(), get()) }

        // Instrument services
        single { InstrumentCreator(get(), get(), get(), get()) }
        single { InstrumentUpdater(get(), get(), get()) }
        single { InstrumentSiresoSaver(get()) }
        single { InstrumentSearcher(get()) }
        single { InstrumentDeleter(get()) }

        // Municipality services
        single { MunicipalityCreator(get(), get()) }
        single { MunicipalityDeleter(get()) }
        single { MunicipalitySearcher(get()) }
        single { MunicipalityUpdater(get(), get()) }

        // Role services

        // Statistic type services
        single { StatisticTypeCreator(get()) }
        single { StatisticTypeDeleter(get(), get()) }
        single { StatisticTypeSearcher(get()) }
        single { StatisticTypeUpdater(get()) }

        // User services
        single { UserSearcher(get()) }
    }
}
