package org.eduardoleolim.core.municipality.infrastructure.bus

import org.eduardoleolim.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.core.municipality.application.searchById.SearchMunicipalityByIdQuery
import org.eduardoleolim.core.municipality.application.searchById.SearchMunicipalityByIdQueryHandler
import org.eduardoleolim.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQueryHandler
import org.eduardoleolim.core.municipality.infrastructure.persistence.KtormMunicipalityRepository
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormMunicipalityQueryHandlers(database: Database) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val municipalityRepository: KtormMunicipalityRepository
    private val federalEntityRepository: KtormFederalEntityRepository
    private val municipalitySearcher: MunicipalitySearcher
    private val federalEntitySearcher: FederalEntitySearcher

    init {
        municipalityRepository = KtormMunicipalityRepository(database)
        federalEntityRepository = KtormFederalEntityRepository(database)
        municipalitySearcher = MunicipalitySearcher(municipalityRepository)
        federalEntitySearcher = FederalEntitySearcher(federalEntityRepository)

        this[SearchMunicipalitiesByTermQuery::class] =
            SearchMunicipalitiesByTermQueryHandler(municipalitySearcher, federalEntitySearcher)
        this[SearchMunicipalityByIdQuery::class] =
            SearchMunicipalityByIdQueryHandler(municipalitySearcher, federalEntitySearcher)
    }
}
