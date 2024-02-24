package org.eduardoleolim.organizadorPec660.app.instrumentType

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypesResponse
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.create.CreateInstrumentTypeCommand
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.delete.DeleteInstrumentTypeCommand
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.searchByTerm.SearchInstrumentTypesByTermQuery
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.update.UpdateInstrumentTypeCommand
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

class InstrumentTypeScreenModel(private val queryBus: QueryBus, private val commandBus: CommandBus) : ScreenModel {
    fun searchInstrumentTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
        callback: (Result<InstrumentTypesResponse>) -> Unit
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            val query = SearchInstrumentTypesByTermQuery(search, orders, limit, offset)

            val result = try {
                Result.success(queryBus.ask<InstrumentTypesResponse>(query))
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun createInstrumentType(name: String, callback: (Result<Unit>) -> Unit) {
        screenModelScope.launch(Dispatchers.IO) {
            val result = try {
                commandBus.dispatch(CreateInstrumentTypeCommand(name))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun editInstrumentType(instrumentTypeId: String, name: String, callback: (Result<Unit>) -> Unit) {
        screenModelScope.launch(Dispatchers.IO) {
            val result = try {
                commandBus.dispatch(UpdateInstrumentTypeCommand(instrumentTypeId, name))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }

    fun deleteInstrumentType(instrumentTypeId: String, callback: (Result<Unit>) -> Unit) {
        screenModelScope.launch(Dispatchers.IO) {
            val result = try {
                commandBus.dispatch(DeleteInstrumentTypeCommand(instrumentTypeId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.cause!!)
            }

            callback(result)
        }
    }
}
