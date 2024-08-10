package org.eduardoleolim.organizadorpec660.app.instrument.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.router.HomeProvider
import org.eduardoleolim.organizadorpec660.core.instrument.application.InstrumentsResponse
import org.eduardoleolim.organizadorpec660.core.instrument.application.save.UpdateInstrumentAsNotSavedCommand
import org.eduardoleolim.organizadorpec660.core.instrument.application.save.UpdateInstrumentAsSavedCommand
import org.eduardoleolim.organizadorpec660.core.instrument.application.searchByTerm.SearchInstrumentsByTermQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class InstrumentScreenModel(
    private val navigator: Navigator,
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var instruments by mutableStateOf(InstrumentsResponse(emptyList(), 0, null, null))
        private set

    fun navigateToSaveInstrumentView(instrumentId: String?) {
        navigator.push(ScreenRegistry.get(HomeProvider.SaveInstrumentScreen(instrumentId)))
    }

    fun searchInstruments(
        search: String? = null,
        federalEntityId: String? = null,
        municipalityId: String? = null,
        agencyId: String? = null,
        statisticTypeId: String? = null,
        statisticYear: Int? = null,
        statisticMonth: Int? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
    ) {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchInstrumentsByTermQuery(
                    federalEntityId,
                    municipalityId,
                    agencyId,
                    statisticTypeId,
                    statisticYear,
                    statisticMonth,
                    search,
                    orders,
                    limit,
                    offset
                )
                instruments = queryBus.ask(query)
            } catch (e: Exception) {
                instruments = InstrumentsResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun updateInstrumentAsSavedInSIRESO(instrumentId: String) {
        commandBus.dispatch(UpdateInstrumentAsSavedCommand(instrumentId))
        searchInstruments()
    }

    fun updateInstrumentAsNotSavedInSIRESO(instrumentId: String) {
        commandBus.dispatch(UpdateInstrumentAsNotSavedCommand(instrumentId))
        searchInstruments()
    }
}
