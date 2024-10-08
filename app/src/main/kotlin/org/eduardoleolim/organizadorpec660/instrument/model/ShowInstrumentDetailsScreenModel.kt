/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.instrument.model

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
import org.eduardoleolim.organizadorpec660.instrument.application.DetailedInstrumentResponse
import org.eduardoleolim.organizadorpec660.instrument.application.searchById.SearchInstrumentByIdQuery
import org.eduardoleolim.organizadorpec660.instrument.views.InstrumentScreen
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
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
