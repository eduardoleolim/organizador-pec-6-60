package org.eduardoleolim.organizadorPec660.core.municipality.infrastructure.bus

import org.eduardoleolim.organizadorPec660.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.organizadorPec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorPec660.core.municipality.application.searchById.SearchMunicipalityByIdQuery
import org.eduardoleolim.organizadorPec660.core.municipality.application.searchById.SearchMunicipalityByIdQueryHandler
import org.eduardoleolim.organizadorPec660.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorPec660.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQueryHandler
import org.eduardoleolim.organizadorPec660.core.municipality.infrastructure.persistence.KtormMunicipalityRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormMunicipalityQueryHandlers(private val database: Database) :
    HashMap<KClass<out Query>, QueryHandler<out Query, out Response>>() {
    private val municipalityRepository = KtormMunicipalityRepository(database)
    private val federalEntityRepository = KtormFederalEntityRepository(database)
    private val municipalitySearcher = MunicipalitySearcher(municipalityRepository)
    private val federalEntitySearcher = FederalEntitySearcher(federalEntityRepository)

    init {
        this[SearchMunicipalitiesByTermQuery::class] = searchByTermQueryHandler()
        this[SearchMunicipalityByIdQuery::class] = searchByIdQueryHandler()
    }

    private fun searchByTermQueryHandler(): QueryHandler<out Query, out Response> {
        val queryHandler = SearchMunicipalitiesByTermQueryHandler(municipalitySearcher, federalEntitySearcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByIdQueryHandler(): QueryHandler<out Query, out Response> {
        val queryHandler = SearchMunicipalityByIdQueryHandler(municipalitySearcher, federalEntitySearcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
