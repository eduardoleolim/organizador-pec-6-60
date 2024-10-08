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

package org.eduardoleolim.organizadorpec660.instrument.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.instrument.data.EmptyInstrumentDataException
import org.eduardoleolim.organizadorpec660.instrument.domain.*
import org.eduardoleolim.organizadorpec660.instrument.model.InstrumentFormState
import org.eduardoleolim.organizadorpec660.instrument.model.SaveInstrumentScreenModel
import org.eduardoleolim.organizadorpec660.shared.composables.*
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource

class SaveInstrumentScreen(
    private val instrumentId: String?,
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val tempDirectory: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel {
            SaveInstrumentScreenModel(navigator, queryBus, commandBus, tempDirectory, Dispatchers.IO)
        }
        var instrumentFilePath by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            SaveInstrumentScreenHeader(
                onCancelRequest = { screenModel.goBackToInstrumentView() }
            )

            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    SaveInstrumentForm(
                        screenModel = screenModel,
                        onInstrumentFilePathSelected = { instrumentFilePath = it }
                    )

                    PdfViewer(
                        pdfPath = instrumentFilePath,
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
    private fun SaveInstrumentScreenHeader(onCancelRequest: () -> Unit) {
        val titleResource = remember {
            if (instrumentId == null) Res.string.inst_form_add_title else Res.string.inst_form_edit_title
        }

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
                text = stringResource(titleResource),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

    @Composable
    private fun SaveInstrumentForm(
        screenModel: SaveInstrumentScreenModel,
        onInstrumentFilePathSelected: (String?) -> Unit
    ) {
        var enabled by remember { mutableStateOf(true) }
        var showErrorDialog by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val verticalScrollState = rememberScrollState()
        val filePickerInteractionSource = screenModel.filePickerInteractionSource

        var isStatisticYearError by remember { mutableStateOf(false) }
        var isStatisticMonthError by remember { mutableStateOf(false) }
        var isMunicipalityError by remember { mutableStateOf(false) }
        var isAgencyError by remember { mutableStateOf(false) }
        var isStatisticTypeError by remember { mutableStateOf(false) }
        var isInstrumentFileError by remember { mutableStateOf(false) }
        var statisticYearSupportingText: String? by remember { mutableStateOf(null) }
        var statisticMonthSupportingText: String? by remember { mutableStateOf(null) }
        var municipalityErrorSupportingText: String? by remember { mutableStateOf(null) }
        var agencyErrorSupportingText: String? by remember { mutableStateOf(null) }
        var statisticTypeSupportingText: String? by remember { mutableStateOf(null) }
        var instrumentFileSupportingText: String? by remember { mutableStateOf(null) }

        val statisticYears = screenModel.statisticYears
        val statisticMonths = screenModel.statisticMonths
        val federalEntities = screenModel.federalEntities
        val municipalities = screenModel.municipalities
        val agencies = screenModel.agencies
        val statisticTypes = screenModel.statisticTypes
        val instrument = screenModel.instrument
        val statisticYear = instrument.statisticYear
        val statisticYearIndex = statisticYears.indexOfFirst { it == statisticYear }.takeIf { it != -1 }
        val statisticMonth = instrument.statisticMonth
        val statisticMonthIndex = statisticMonths.indexOfFirst { it.first == statisticMonth?.first }.takeIf { it != -1 }
        val federalEntity = instrument.federalEntity
        val federalEntityIndex = federalEntities.indexOfFirst { it.id == federalEntity?.id }.takeIf { it != -1 }
        val municipality = instrument.municipality
        val municipalityIndex = municipalities.indexOfFirst { it.id == municipality?.id }.takeIf { it != -1 }
        val agency = instrument.agency
        val agencyIndex = agencies.indexOfFirst { it.id == agency?.id }.takeIf { it != -1 }
        val statisticType = instrument.statisticType
        val statisticTypeIndex = statisticTypes.indexOfFirst { it.id == statisticType?.id }.takeIf { it != -1 }
        val instrumentFilePath = instrument.instrumentFilePath

        LaunchedEffect(Unit) {
            screenModel.searchAllFederalEntities()
            screenModel.searchInstrument(instrumentId)
        }

        LaunchedEffect(federalEntity) {
            screenModel.searchMunicipalities(federalEntity?.id)
        }

        LaunchedEffect(municipality) {
            screenModel.searchAgencies(municipality?.id)
        }

        LaunchedEffect(agency) {
            screenModel.searchStatisticTypes(agency?.id)
        }

        LaunchedEffect(instrumentFilePath) {
            onInstrumentFilePathSelected(instrumentFilePath)
        }

        when (val formState = screenModel.formState) {
            InstrumentFormState.Idle -> {
                enabled = true
                showErrorDialog = false
                errorMessage = null
                isStatisticYearError = false
                isStatisticMonthError = false
                isMunicipalityError = false
                isAgencyError = false
                isStatisticTypeError = false
                isInstrumentFileError = false
                statisticYearSupportingText = null
                statisticMonthSupportingText = null
                municipalityErrorSupportingText = null
                agencyErrorSupportingText = null
                statisticTypeSupportingText = null
                instrumentFileSupportingText = null
            }

            InstrumentFormState.InProgress -> {
                enabled = false
                showErrorDialog = false
                errorMessage = null
                isStatisticYearError = false
                isStatisticMonthError = false
                isMunicipalityError = false
                isAgencyError = false
                isStatisticTypeError = false
                isInstrumentFileError = false
                statisticYearSupportingText = null
                statisticMonthSupportingText = null
                municipalityErrorSupportingText = null
                agencyErrorSupportingText = null
                statisticTypeSupportingText = null
                instrumentFileSupportingText = null
            }

            InstrumentFormState.SuccessCreate -> {
                enabled = true
                showErrorDialog = false
                errorMessage = null
            }

            InstrumentFormState.SuccessEdit -> {
                enabled = true
                screenModel.goBackToInstrumentView()
            }

            is InstrumentFormState.Error -> {
                enabled = true
                when (val error = formState.error) {
                    is AgencyNotFoundError -> {
                        showErrorDialog = true
                        errorMessage = stringResource(Res.string.inst_form_error_agency_not_found)
                    }

                    is StatisticTypeNotFoundError -> {
                        showErrorDialog = true
                        errorMessage = stringResource(Res.string.inst_form_error_statistic_type_not_found)
                    }

                    is MunicipalityNotFoundError -> {
                        showErrorDialog = true
                        errorMessage = stringResource(Res.string.inst_form_error_municipality_not_found)
                    }

                    is InstrumentAlreadyExistsError -> {
                        showErrorDialog = true
                        errorMessage = stringResource(Res.string.inst_form_error_instrument_already_exists)
                    }

                    is InstrumentFileRequiredError -> {
                        showErrorDialog = true
                        errorMessage = stringResource(Res.string.inst_form_error_instrument_file_required)
                    }

                    is InstrumentFileFailSaveError -> {
                        showErrorDialog = true
                        errorMessage = stringResource(Res.string.inst_form_error_instrument_file_save_error)
                    }

                    is EmptyInstrumentDataException -> {
                        if (error.isStatisticYearUnselected) {
                            isStatisticYearError = true
                            statisticYearSupportingText = stringResource(Res.string.inst_form_year_required)
                        }

                        if (error.isStatisticMonthUnselected) {
                            isStatisticMonthError = true
                            statisticMonthSupportingText = stringResource(Res.string.inst_form_month_required)
                        }

                        if (error.isMunicipalityUnselected) {
                            isMunicipalityError = true
                            municipalityErrorSupportingText = stringResource(Res.string.inst_form_municipality_required)
                        }

                        if (error.isAgencyUnselected) {
                            isAgencyError = true
                            agencyErrorSupportingText = stringResource(Res.string.inst_form_agency_required)
                        }

                        if (error.isStatisticTypeUnselected) {
                            isStatisticTypeError = true
                            statisticTypeSupportingText = stringResource(Res.string.inst_form_statistic_type_required)
                        }

                        if (error.isInstrumentFileUnselected) {
                            isInstrumentFileError = true
                            instrumentFileSupportingText = stringResource(Res.string.inst_form_document_required)
                        }
                    }

                    else -> {
                        showErrorDialog = true
                        errorMessage = stringResource(Res.string.inst_form_error_default)
                    }
                }

                println(formState.error.message)
                println(formState.error.cause)
            }
        }

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
                OutlinedFilePicker(
                    enabled = enabled,
                    value = instrumentFilePath,
                    label = {
                        Text(
                            text = stringResource(Res.string.inst_document),
                            maxLines = 1
                        )
                    },
                    onFileSelected = { instrumentFilePath ->
                        screenModel.updateInstrumentInstrumentFilePath(instrumentFilePath)
                    },
                    interactionSource = filePickerInteractionSource,
                    extensions = listOf(NameExtension("Documentos PDF (*.pdf)", "pdf", true)),
                    isError = isInstrumentFileError,
                    supportingText = instrumentFileSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(
                            text = stringResource(Res.string.inst_year),
                            maxLines = 1
                        )
                    },
                    items = statisticYears,
                    index = statisticYearIndex,
                    onValueSelected = { _, statisticYear ->
                        screenModel.updateInstrumentStatisticYear(statisticYear)
                    },
                    isError = isStatisticYearError,
                    supportingText = statisticYearSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(
                            text = stringResource(Res.string.inst_month),
                            maxLines = 1
                        )
                    },
                    items = statisticMonths,
                    index = statisticMonthIndex,
                    onValueSelected = { _, statisticMonth ->
                        screenModel.updateInstrumentStatisticMonth(statisticMonth)
                    },
                    visualTransformation = { it.second },
                    isError = isStatisticMonthError,
                    supportingText = statisticMonthSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(
                            text = stringResource(Res.string.inst_federal_entity),
                            maxLines = 1
                        )
                    },
                    items = federalEntities,
                    index = federalEntityIndex,
                    onValueSelected = { _, federalEntity ->
                        screenModel.updateInstrumentFederalEntity(federalEntity)
                    },
                    visualTransformation = { "${it.keyCode} - ${it.name}" }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(
                            text = stringResource(Res.string.inst_municipality),
                            maxLines = 1
                        )
                    },
                    items = municipalities,
                    index = municipalityIndex,
                    onValueSelected = { _, municipality ->
                        screenModel.updateInstrumentMunicipality(municipality)
                    },
                    visualTransformation = { "${it.keyCode} - ${it.name}" },
                    isError = isMunicipalityError,
                    supportingText = municipalityErrorSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(
                            text = stringResource(Res.string.inst_agency),
                            maxLines = 1
                        )
                    },
                    items = screenModel.agencies,
                    index = agencyIndex,
                    onValueSelected = { _, agency ->
                        screenModel.updateInstrumentAgency(agency)
                    },
                    visualTransformation = { "${it.consecutive} - ${it.name}" },
                    isError = isAgencyError,
                    supportingText = agencyErrorSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(
                            text = stringResource(Res.string.inst_statistic_type),
                            maxLines = 1
                        )
                    },
                    items = statisticTypes,
                    index = statisticTypeIndex,
                    onValueSelected = { _, statisticType ->
                        screenModel.updateInstrumentStatisticType(statisticType)
                    },
                    visualTransformation = { "${it.keyCode} - ${it.name}" },
                    isError = isStatisticTypeError,
                    supportingText = statisticTypeSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    }
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { screenModel.saveInstrument() }
                ) {
                    Text(stringResource(Res.string.save))
                }
            }
        }

        if (showErrorDialog) {
            ErrorDialog(
                text = {
                    errorMessage?.let {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(it)
                        }
                    }
                },
                onDismissRequest = { screenModel.resetState() },
                onConfirmRequest = { screenModel.resetState() }
            )
        }
    }
}
