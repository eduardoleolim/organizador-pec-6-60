package org.eduardoleolim.organizadorpec660.instrument.views

import androidx.compose.foundation.interaction.MutableInteractionSource
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
import kotlinx.coroutines.launch
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
        val coroutineScope = rememberCoroutineScope()
        var enabled by remember { mutableStateOf(true) }
        val verticalScrollState = rememberScrollState()
        val filePickerInteractionSource = remember { MutableInteractionSource() }

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

        LaunchedEffect(screenModel.formState) {
            coroutineScope.launch {
                if (screenModel.formState is InstrumentFormState.SuccessCreate) {
                    filePickerInteractionSource.emit(ResetFilePickerInteraction)
                    screenModel.updateInstrumentInstrumentFilePath(null)
                }
            }
        }

        when (val formState = screenModel.formState) {
            InstrumentFormState.Idle -> {
                enabled = true
            }

            InstrumentFormState.InProgress -> {
                enabled = false
            }

            InstrumentFormState.SuccessCreate -> {
                enabled = true
            }

            InstrumentFormState.SuccessEdit -> {
                enabled = true
                screenModel.goBackToInstrumentView()
            }

            is InstrumentFormState.Error -> {
                enabled = true
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
                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_year))
                    },
                    items = statisticYears,
                    index = statisticYearIndex,
                    onValueSelected = { _, statisticYear ->
                        screenModel.updateInstrumentStatisticYear(statisticYear)
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_month))
                    },
                    items = statisticMonths,
                    index = statisticMonthIndex,
                    onValueSelected = { _, statisticMonth ->
                        screenModel.updateInstrumentStatisticMonth(statisticMonth)
                    },
                    visualTransformation = { it.second }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_federal_entity))
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
                        Text(stringResource(Res.string.inst_municipality))
                    },
                    items = municipalities,
                    index = municipalityIndex,
                    onValueSelected = { _, municipality ->
                        screenModel.updateInstrumentMunicipality(municipality)
                    },
                    visualTransformation = { "${it.keyCode} - ${it.name}" }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_agency))
                    },
                    items = screenModel.agencies,
                    index = agencyIndex,
                    onValueSelected = { _, agency ->
                        screenModel.updateInstrumentAgency(agency)
                    },
                    visualTransformation = { "${it.consecutive} - ${it.name}" }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_statistic_type))
                    },
                    items = statisticTypes,
                    index = statisticTypeIndex,
                    onValueSelected = { _, statisticType ->
                        screenModel.updateInstrumentStatisticType(statisticType)
                    },
                    visualTransformation = { "${it.keyCode} - ${it.name}" }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedFilePicker(
                    enabled = enabled,
                    value = instrumentFilePath,
                    label = {
                        Text(stringResource(Res.string.inst_document))
                    },
                    onFileSelected = { instrumentFilePath ->
                        screenModel.updateInstrumentInstrumentFilePath(instrumentFilePath)
                    },
                    interactionSource = filePickerInteractionSource,
                    extensions = listOf(NameExtension("Documentos PDF (*.pdf)", "pdf", true))
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
    }
}
