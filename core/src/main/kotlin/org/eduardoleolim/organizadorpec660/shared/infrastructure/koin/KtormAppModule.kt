/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.shared.infrastructure.koin

import org.eduardoleolim.organizadorpec660.agency.application.create.AgencyCreator
import org.eduardoleolim.organizadorpec660.agency.application.delete.AgencyDeleter
import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.agency.application.update.AgencyUpdater
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.agency.infrastructure.persistence.KtormAgencyRepository
import org.eduardoleolim.organizadorpec660.auth.application.authenticate.UserAuthenticator
import org.eduardoleolim.organizadorpec660.auth.domain.AuthRepository
import org.eduardoleolim.organizadorpec660.auth.infrastructure.persistence.KtormAuthRepository
import org.eduardoleolim.organizadorpec660.federalEntity.application.create.FederalEntityCreator
import org.eduardoleolim.organizadorpec660.federalEntity.application.delete.FederalEntityDeleter
import org.eduardoleolim.organizadorpec660.federalEntity.application.importer.FederalEntityImporter
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.application.update.FederalEntityUpdater
import org.eduardoleolim.organizadorpec660.federalEntity.domain.CsvFederalEntityImportInput
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityImportReader
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.services.CsvFederalEntityImportReader
import org.eduardoleolim.organizadorpec660.instrument.application.create.InstrumentCreator
import org.eduardoleolim.organizadorpec660.instrument.application.delete.InstrumentDeleter
import org.eduardoleolim.organizadorpec660.instrument.application.importer.InstrumentFromV1Importer
import org.eduardoleolim.organizadorpec660.instrument.application.save.InstrumentSiresoSaver
import org.eduardoleolim.organizadorpec660.instrument.application.search.InstrumentSearcher
import org.eduardoleolim.organizadorpec660.instrument.application.update.InstrumentUpdater
import org.eduardoleolim.organizadorpec660.instrument.domain.AccdbInstrumentImportInput
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentImportReader
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.instrument.infrastructure.persistence.KtormInstrumentRepository
import org.eduardoleolim.organizadorpec660.instrument.infrastructure.services.AccdbInstrumentImportReader
import org.eduardoleolim.organizadorpec660.municipality.application.create.MunicipalityCreator
import org.eduardoleolim.organizadorpec660.municipality.application.delete.MunicipalityDeleter
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.application.update.MunicipalityUpdater
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.municipality.infrastructure.persistence.KtormMunicipalityRepository
import org.eduardoleolim.organizadorpec660.statisticType.application.create.StatisticTypeCreator
import org.eduardoleolim.organizadorpec660.statisticType.application.delete.StatisticTypeDeleter
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.application.update.StatisticTypeUpdater
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeRepository
import org.eduardoleolim.organizadorpec660.statisticType.infrastructure.persistence.KtormStatisticTypeRepository
import org.eduardoleolim.organizadorpec660.user.application.search.UserSearcher
import org.eduardoleolim.organizadorpec660.user.domain.UserRepository
import org.eduardoleolim.organizadorpec660.user.infrastructure.persistence.KtormUserRepository
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
        single<InstrumentImportReader<AccdbInstrumentImportInput>> { AccdbInstrumentImportReader() }
        single<InstrumentFromV1Importer<AccdbInstrumentImportInput>> {
            InstrumentFromV1Importer(
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }

        // Municipality services
        single { MunicipalityCreator(get(), get()) }
        single { MunicipalityDeleter(get(), get()) }
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
