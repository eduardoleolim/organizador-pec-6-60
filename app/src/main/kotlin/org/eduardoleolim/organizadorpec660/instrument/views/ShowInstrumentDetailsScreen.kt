/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.organizadorpec660.instrument.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.instrument.application.DetailedInstrumentResponse
import org.eduardoleolim.organizadorpec660.instrument.model.ShowInstrumentDetailsScreenModel
import org.eduardoleolim.organizadorpec660.shared.composables.PdfViewer
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource
import java.text.DateFormatSymbols

class ShowInstrumentDetailsScreen(
    private val instrumentId: String,
    private val queryBus: QueryBus,
    private val tempDirectory: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel {
            ShowInstrumentDetailsScreenModel(navigator, queryBus, tempDirectory, Dispatchers.IO)
        }
        val instrument = screenModel.instrument
        val tempInstrumentFile = screenModel.tempInstrumentFile

        LaunchedEffect(Unit) {
            screenModel.searchInstrument(instrumentId)
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            ShowInstrumentDetailsScreenHeader(
                onCancelRequest = { screenModel.goBackToInstrumentView() }
            )

            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    InstrumentsInfo(instrument)

                    PdfViewer(
                        pdfPath = tempInstrumentFile?.absolutePath,
                        isReaderMode = true,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        topBarColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        pageColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                }
            }
        }
    }

    @Composable
    private fun ShowInstrumentDetailsScreenHeader(onCancelRequest: () -> Unit) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancelRequest
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }

            Text(
                text = stringResource(Res.string.inst_show_details_title),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

    @Composable
    private fun InstrumentsInfo(instrument: DetailedInstrumentResponse?) {
        val verticalScrollState = rememberScrollState()
        val months = remember { DateFormatSymbols().months.take(12) }
        val month = remember(instrument) {
            instrument?.statisticMonth?.let { months[it - 1] }?.uppercase() ?: ""
        }
        val federalEntity = instrument?.federalEntity
        val municipality = instrument?.municipality
        val agency = instrument?.agency
        val statisticType = instrument?.statisticType
        val savedResource =
            remember(instrument) { instrument?.let { if (instrument.saved) Res.string.inst_in_sireso_yes else Res.string.inst_in_sireso_no } }

        Surface(
            modifier = Modifier
                .width(300.dp)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
                    .verticalScroll(verticalScrollState)
            ) {
                OutlinedTextField(
                    value = "${instrument?.statisticYear ?: ""}",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(stringResource(Res.string.inst_year))
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = month,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(stringResource(Res.string.inst_month))
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = federalEntity?.let { "${it.keyCode} - ${it.name}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(stringResource(Res.string.inst_federal_entity))
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = municipality?.let { "${it.keyCode} - ${it.name}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(stringResource(Res.string.inst_municipality))
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = agency?.let { "${it.consecutive} - ${it.name}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(stringResource(Res.string.inst_agency))
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = statisticType?.let { "${it.keyCode} - ${it.name}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(stringResource(Res.string.inst_statistic_type))
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = savedResource?.let { stringResource(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(stringResource(Res.string.inst_in_sireso))
                    }
                )
            }
        }
    }
}
