package org.eduardoleolim.organizadorpec660.app.federalEntity

import cafe.adriel.voyager.core.model.ScreenModel
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.create.CreateFederalEntityCommand
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.delete.DeleteFederalEntityCommand
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.update.UpdateFederalEntityCommand
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import kotlin.concurrent.thread

class FederalEntityScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    fun searchFederalEntities(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
        callback: (Result<FederalEntitiesResponse>) -> Unit
    ) {
        thread(start = true) {
            val query = SearchFederalEntitiesByTermQuery(search, orders, limit, offset)

            val result = try {
                Result.success(queryBus.ask<FederalEntitiesResponse>(query))
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun createFederalEntity(keyCode: String, name: String, callback: (Result<Unit>) -> Unit) {
        thread(start = true) {
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
        thread(start = true) {
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
        thread(start = true) {
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
