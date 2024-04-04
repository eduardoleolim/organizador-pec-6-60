package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.koin

import org.eduardoleolim.organizadorPec660.core.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorPec660.core.agency.infrastructure.persistence.KtormAgencyRepository
import org.eduardoleolim.organizadorPec660.core.auth.application.authenticate.UserAuthenticator
import org.eduardoleolim.organizadorPec660.core.auth.domain.AuthRepository
import org.eduardoleolim.organizadorPec660.core.auth.infrastructure.persistence.KtormAuthRepository
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.create.FederalEntityCreator
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.delete.FederalEntityDeleter
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.update.FederalEntityUpdater
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.organizadorPec660.core.instrument.application.create.InstrumentCreator
import org.eduardoleolim.organizadorPec660.core.instrument.application.update.InstrumentUpdater
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.create.InstrumentTypeCreator
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.delete.InstrumentTypeDeleter
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.search.InstrumentTypeSearcher
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.update.InstrumentTypeUpdater
import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentTypeRepository
import org.eduardoleolim.organizadorPec660.core.instrumentType.infrastructure.persistence.KtormInstrumentTypeRepository
import org.eduardoleolim.organizadorPec660.core.municipality.application.create.MunicipalityCreator
import org.eduardoleolim.organizadorPec660.core.municipality.application.delete.MunicipalityDeleter
import org.eduardoleolim.organizadorPec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorPec660.core.municipality.application.update.MunicipalityUpdater
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorPec660.core.municipality.infrastructure.persistence.KtormMunicipalityRepository
import org.eduardoleolim.organizadorPec660.core.statisticType.application.create.StatisticTypeCreator
import org.eduardoleolim.organizadorPec660.core.statisticType.application.delete.StatisticTypeDeleter
import org.eduardoleolim.organizadorPec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorPec660.core.statisticType.application.update.StatisticTypeUpdater
import org.eduardoleolim.organizadorPec660.core.statisticType.domain.StatisticTypeRepository
import org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.persistence.KtormStatisticTypeRepository
import org.eduardoleolim.organizadorPec660.core.user.application.search.UserSearcher
import org.eduardoleolim.organizadorPec660.core.user.domain.UserRepository
import org.eduardoleolim.organizadorPec660.core.user.infrastructure.persistence.KtormUserRepository
import org.koin.dsl.module
import org.ktorm.database.Database


object KtormAppModule {
    fun buildModule(database: Database) = module {
        single<Database> { database }

        // Repositories
        single<AgencyRepository> { KtormAgencyRepository(get()) }
        single<AuthRepository> { KtormAuthRepository(get()) }
        single<FederalEntityRepository> { KtormFederalEntityRepository(get()) }
        // single<InstrumentFileRepository> {  }
        // single<InstrumentRepository> {  }
        single<InstrumentTypeRepository> { KtormInstrumentTypeRepository(get()) }
        single<MunicipalityRepository> { KtormMunicipalityRepository(get()) }
        // single<RoleRepository> { KtormRoleRepository(get()) }
        single<StatisticTypeRepository> { KtormStatisticTypeRepository(get()) }
        single<UserRepository> { KtormUserRepository(get()) }

        // Auth services
        single { UserAuthenticator(get()) }

        // Federal Entity services
        single { FederalEntityCreator(get()) }
        single { FederalEntityDeleter(get()) }
        single { FederalEntitySearcher(get()) }
        single { FederalEntityUpdater(get()) }

        // Instrument services
        single { InstrumentCreator(get(), get(), get(), get(), get()) }
        single { InstrumentUpdater(get(), get(), get(), get()) }

        // Instrument type services
        single { InstrumentTypeCreator(get()) }
        single { InstrumentTypeDeleter(get()) }
        single { InstrumentTypeSearcher(get()) }
        single { InstrumentTypeUpdater(get()) }

        // Municipality services
        single { MunicipalityCreator(get(), get()) }
        single { MunicipalityDeleter(get()) }
        single { MunicipalitySearcher(get()) }
        single { MunicipalityUpdater(get(), get()) }

        // Role services

        // Statistic type services
        single { StatisticTypeCreator(get(), get()) }
        single { StatisticTypeDeleter(get()) }
        single { StatisticTypeSearcher(get()) }
        single { StatisticTypeUpdater(get()) }

        // User services
        single { UserSearcher(get()) }
    }
}
