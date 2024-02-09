package org.eduardoleolim.organizadorpec660.app.federalEntity

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.delete.DeleteFederalEntityCommand
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class FederalEntityScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    fun searchFederalEntities(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
        callback: (Result<FederalEntitiesResponse>) -> Unit
    ) {
        screenModelScope.launch {
            val query = SearchFederalEntitiesByTermQuery(search, orders, limit, offset)

            try {
                callback(Result.success(queryBus.ask(query)))
            } catch (e: Exception) {
                callback(Result.failure(e.cause!!))
            }
        }
    }

    fun deleteFederalEntity(federalEntityId: String, callback: (Result<Unit>) -> Unit) {
        screenModelScope.launch {
            try {
                commandBus.dispatch(DeleteFederalEntityCommand(federalEntityId))
                callback(Result.success(Unit))
            } catch (e: Exception) {
                callback(Result.failure(e.cause!!))
            }
        }
    }
}
