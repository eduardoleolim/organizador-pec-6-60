package org.eduardoleolim.organizadorpec660.app.municipality

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.delete.DeleteMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.core.municipality.application.update.UpdateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import kotlin.concurrent.thread

class MunicipalityScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    fun searchMunicipalities(
        search: String? = null,
        federalEntityId: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
        callback: (Result<MunicipalitiesResponse>) -> Unit
    ) {
        thread(start = true) {
            val query = SearchMunicipalitiesByTermQuery(federalEntityId, search, orders, limit, offset)

            val result = try {
                Result.success(queryBus.ask<MunicipalitiesResponse>(query))
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun allFederalEntities(callback: (Result<List<FederalEntityResponse>>) -> Unit) {
        thread(start = true) {
            val result = try {
                val federalEntities = queryBus.ask<FederalEntitiesResponse>(SearchFederalEntitiesByTermQuery())
                Result.success(federalEntities.federalEntities)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun createMunicipality(
        keyCode: String,
        name: String,
        federalEntityId: String,
        callback: (Result<Unit>) -> Unit
    ) {
        thread(start = true) {
            val result = try {
                commandBus.dispatch(CreateMunicipalityCommand(keyCode, name, federalEntityId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun editMunicipality(
        municipalityId: String,
        keyCode: String,
        name: String,
        federalEntityId: String,
        callback: (Result<Unit>) -> Unit
    ) {
        thread(start = true) {
            val result = try {
                commandBus.dispatch(UpdateMunicipalityCommand(municipalityId, keyCode, name, federalEntityId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun deleteMunicipality(municipalityId: String, callback: (Result<Unit>) -> Unit) {
        thread(start = true) {
            val result = try {
                commandBus.dispatch(DeleteMunicipalityCommand(municipalityId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun launch(block: () -> Unit) {
        screenModelScope.launch {
            block()
        }
    }
}
