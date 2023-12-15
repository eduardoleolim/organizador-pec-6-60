package org.eduardoleolim.organizadorpec660.app.federalEntity

import cafe.adriel.voyager.core.model.ScreenModel
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class FederalEntityScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    fun searchFederalEntities(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ): Result<FederalEntitiesResponse> {
        return SearchFederalEntitiesByTermQuery(search, orders, limit, offset).let {
            try {
                Result.success(queryBus.ask(it))
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }
        }
    }
}
