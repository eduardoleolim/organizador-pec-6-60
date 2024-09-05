package org.eduardoleolim.organizadorpec660.app.instrument.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.instrument.views.InstrumentScreen
import org.eduardoleolim.organizadorpec660.core.instrument.application.DetailedInstrumentResponse
import org.eduardoleolim.organizadorpec660.core.instrument.application.searchById.SearchInstrumentByIdQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import java.io.File

class ShowInstrumentDetailsScreenModel(
    private val navigator: Navigator,
    private val queryBus: QueryBus,
    private val tempDirectory: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var instrument by mutableStateOf<DetailedInstrumentResponse?>(null)
        private set

    var tempInstrumentFile by mutableStateOf<File?>(null)

    fun goBackToInstrumentView() {
        navigator.popUntil<InstrumentScreen, Screen>()
    }

    fun searchInstrument(instrumentId: String) {
        screenModelScope.launch(dispatcher) {
            delay(1000)
            instrument = queryBus.ask<DetailedInstrumentResponse>(SearchInstrumentByIdQuery(instrumentId)).also {
                tempInstrumentFile = File(tempDirectory).resolve("edit/${it.filename}.pdf").apply {
                    parentFile.mkdirs()
                    writeBytes(it.instrumentFile.content)
                }
            }
        }
    }
}
