package org.eduardoleolim.organizadorpec660.app.instrument.views

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
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.instrument.model.InstrumentFormState
import org.eduardoleolim.organizadorpec660.app.instrument.model.SaveInstrumentScreenModel
import org.eduardoleolim.organizadorpec660.app.shared.composables.*
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.jetbrains.compose.resources.stringResource
import java.text.DateFormatSymbols
import java.time.LocalDate

class SaveInstrumentScreen(
    private val instrumentId: String?,
    private val queryBus: QueryBus,
    private val commandBus: CommandBus
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SaveInstrumentScreenModel(navigator, queryBus, commandBus) }
        var instrumentSelected by remember { mutableStateOf<String?>(null) }

        Column(modifier = Modifier.padding(24.dp)) {
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
                    InstrumentForm(
                        screenModel = screenModel,
                        onInstrumentSelected = { instrumentSelected = it }
                    )

                    PdfViewer(
                        pdfPath = instrumentSelected,
                        isReaderMode = true,
                        modifier = Modifier.fillMaxHeight()
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
        val title = if (instrumentId == null) {
            stringResource(Res.string.inst_form_add_title)
        } else {
            stringResource(Res.string.inst_form_edit_title)
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
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

    @Composable
    private fun InstrumentForm(
        screenModel: SaveInstrumentScreenModel,
        onInstrumentSelected: (String?) -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        var enabled by remember { mutableStateOf(true) }
        val scrollState = rememberScrollState()
        val filePickerInteractionSource = remember { MutableInteractionSource() }
        val years = remember { (LocalDate.now().year downTo 1983).toList() }
        val months = remember {
            DateFormatSymbols().months.mapIndexedNotNull { index, month ->
                if (index == 12) {
                    null
                } else {
                    Pair(index + 1, month.uppercase())
                }
            }
        }

        var year by remember { mutableStateOf<Int?>(null) }
        var month by remember { mutableStateOf<Int?>(null) }
        var federalEntityId by remember { mutableStateOf<String?>(null) }
        var federalEntityIndex by remember { mutableStateOf<Int?>(null) }
        var municipalityId by remember { mutableStateOf<String?>(null) }
        var municipalityIndex by remember { mutableStateOf<Int?>(null) }
        var agencyId by remember { mutableStateOf<String?>(null) }
        var agencyIndex by remember { mutableStateOf<Int?>(null) }
        var statisticTypeId by remember { mutableStateOf<String?>(null) }
        var statisticTypeIndex by remember { mutableStateOf<Int?>(null) }
        var documentPath by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            screenModel.searchAllFederalEntities()
        }

        LaunchedEffect(federalEntityId) {
            screenModel.searchMunicipalities(federalEntityId)
            municipalityIndex = null
            municipalityId = null
        }

        LaunchedEffect(municipalityId) {
            screenModel.searchAgencies(municipalityId)
            agencyIndex = null
            agencyId = null
        }

        LaunchedEffect(agencyId) {
            screenModel.searchStatisticTypes(agencyId)
            statisticTypeIndex = null
            statisticTypeId = null
        }

        LaunchedEffect(documentPath) {
            onInstrumentSelected(documentPath)
        }

        LaunchedEffect(screenModel.formState) {
            coroutineScope.launch {
                if (screenModel.formState is InstrumentFormState.SuccessCreate) {
                    filePickerInteractionSource.emit(ResetFilePickerInteraction)
                    documentPath = null
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
            modifier = Modifier.width(300.dp)
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_year))
                    },
                    items = years,
                    onValueSelected = { _, it ->
                        year = it
                    }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_month))
                    },
                    items = months,
                    onValueSelected = { _, it ->
                        month = it.first
                    },
                    visualTransformation = { it.second }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_federal_entity))
                    },
                    items = screenModel.federalEntities,
                    index = federalEntityIndex,
                    onValueSelected = { index, federalEntity ->
                        federalEntityIndex = index
                        federalEntityId = federalEntity.id
                    },
                    visualTransformation = { "${it.keyCode} - ${it.name}" }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_municipality))
                    },
                    items = screenModel.municipalities,
                    index = municipalityIndex,
                    onValueSelected = { index, municipality ->
                        municipalityIndex = index
                        municipalityId = municipality.id
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
                    onValueSelected = { index, agency ->
                        agencyIndex = index
                        agencyId = agency.id
                    },
                    visualTransformation = { "${it.consecutive} - ${it.name}" }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedSelect(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_statistic_type))
                    },
                    items = screenModel.statisticTypes,
                    index = statisticTypeIndex,
                    onValueSelected = { index, statisticType ->
                        statisticTypeIndex = index
                        statisticTypeId = statisticType.id
                    },
                    visualTransformation = { "${it.keyCode} - ${it.name}" }
                )

                Spacer(Modifier.height(24.dp))

                OutlinedFilePicker(
                    enabled = enabled,
                    label = {
                        Text(stringResource(Res.string.inst_document))
                    },
                    onFileSelected = { documentPath = it },
                    interactionSource = filePickerInteractionSource,
                    extensions = listOf(NameExtension("Documentos PDF (*.pdf)", "pdf", true))
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    enabled = enabled,
                    onClick = {
                        if (instrumentId == null) {
                            screenModel.saveInstrument(
                                year,
                                month,
                                municipalityId,
                                agencyId,
                                statisticTypeId,
                                documentPath
                            )
                        } else {
                            screenModel.editInstrument(
                                instrumentId,
                                year,
                                month,
                                municipalityId,
                                agencyId,
                                statisticTypeId
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.save))
                }
            }
        }
    }
}
