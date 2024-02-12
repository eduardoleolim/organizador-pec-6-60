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

class MunicipalityScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    fun searchMunicipalities(
        search: String? = null,
        federalEntityId: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
        callback: (Result<MunicipalitiesResponse>) -> Unit
    ) {
        screenModelScope.launch {
            val query = SearchMunicipalitiesByTermQuery(federalEntityId, search, orders, limit, offset)

            try {
                callback(Result.success(queryBus.ask(query)))
            } catch (e: Exception) {
                callback(Result.failure(e.cause!!))
            }
        }
    }

    fun allFederalEntities(callback: (Result<List<FederalEntityResponse>>) -> Unit) {
        screenModelScope.launch {
            try {
                val federalEntities = queryBus.ask<FederalEntitiesResponse>(SearchFederalEntitiesByTermQuery())
                callback(Result.success(federalEntities.federalEntities))
            } catch (e: Exception) {
                callback(Result.failure(e.cause!!))
            }
        }
    }

    fun createMunicipality(
        keyCode: String,
        name: String,
        federalEntityId: String,
        callback: (Result<Unit>) -> Unit
    ) {
        screenModelScope.launch {
            try {
                commandBus.dispatch(CreateMunicipalityCommand(keyCode, name, federalEntityId))
                callback(Result.success(Unit))
            } catch (e: Exception) {
                callback(Result.failure(e.cause!!))
            }
        }
    }

    fun editMunicipality(
        municipalityId: String,
        keyCode: String,
        name: String,
        federalEntityId: String,
        callback: (Result<Unit>) -> Unit
    ) {
        screenModelScope.launch {
            try {
                commandBus.dispatch(UpdateMunicipalityCommand(municipalityId, keyCode, name, federalEntityId))
                callback(Result.success(Unit))
            } catch (e: Exception) {
                callback(Result.failure(e.cause!!))
            }
        }
    }

    fun deleteMunicipality(municipalityId: String, callback: (Result<Unit>) -> Unit) {
        screenModelScope.launch {
            try {
                commandBus.dispatch(DeleteMunicipalityCommand(municipalityId))
                callback(Result.success(Unit))
            } catch (e: Exception) {
                callback(Result.failure(e.cause!!))
            }
        }
    }
}
