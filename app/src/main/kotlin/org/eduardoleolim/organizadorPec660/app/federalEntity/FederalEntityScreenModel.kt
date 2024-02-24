package org.eduardoleolim.organizadorPec660.app.federalEntity

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.create.CreateFederalEntityCommand
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.delete.DeleteFederalEntityCommand
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.update.UpdateFederalEntityCommand
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

class FederalEntityScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    private var _federalEntities = mutableStateOf(FederalEntitiesResponse(emptyList(), 0, null, null))
    val federalEntities: State<FederalEntitiesResponse> get() = _federalEntities

    fun searchFederalEntities(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val query = SearchFederalEntitiesByTermQuery(search, orders, limit, offset)
                _federalEntities.value = queryBus.ask(query)
            } catch (e: Exception) {
                _federalEntities.value = FederalEntitiesResponse(emptyList(), 0, null, null)
                println(e.message)
            }
        }
    }

    fun createFederalEntity(keyCode: String, name: String, callback: (Result<Unit>) -> Unit) {
        screenModelScope.launch(Dispatchers.IO) {
            val result = try {
                commandBus.dispatch(CreateFederalEntityCommand(keyCode, name))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun editFederalEntity(federalEntityId: String, keyCode: String, name: String, callback: (Result<Unit>) -> Unit) {
        screenModelScope.launch(Dispatchers.IO) {
            val result = try {
                commandBus.dispatch(UpdateFederalEntityCommand(federalEntityId, keyCode, name))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun deleteFederalEntity(federalEntityId: String, callback: (Result<Unit>) -> Unit) {
        screenModelScope.launch(Dispatchers.IO) {
            val result = try {
                commandBus.dispatch(DeleteFederalEntityCommand(federalEntityId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }
}
