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

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.v2.maxScrollOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.eduardoleolim.organizadorpec660.instrument.domain.CanNotImportInstrumentsError
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentImportDataFields
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentImportFieldNotFound
import org.eduardoleolim.organizadorpec660.instrument.model.InstrumentImportState
import org.eduardoleolim.organizadorpec660.instrument.model.InstrumentScreenModel
import org.eduardoleolim.organizadorpec660.shared.composables.ErrorDialog
import org.eduardoleolim.organizadorpec660.shared.dialogs.Dialogs
import org.eduardoleolim.organizadorpec660.shared.dialogs.Dialogs.Option
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

private enum class ImportType {
    FROM_TEMPLATE,
    FROM_V1,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrumentScreen.InstrumentImportModal(
    screenModel: InstrumentScreenModel,
    onSuccessImport: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var importType by remember { mutableStateOf(ImportType.FROM_TEMPLATE) }
    var warnings by remember { mutableStateOf(emptyList<String>()) }

    var enable by remember { mutableStateOf(true) }
    var showWarningDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            screenModel.resetImportModal()
        }
    }

    when (val importState = screenModel.importState) {
        InstrumentImportState.Idle -> {
            enable = true
            showWarningDialog = false
        }

        InstrumentImportState.InProgress -> {
            enable = false
            showWarningDialog = false
        }

        is InstrumentImportState.Success -> {
            if (importState.warnings.isEmpty()) {
                onSuccessImport()
            } else {
                warnings = importState.warnings.mapNotNull { it.message }
                showWarningDialog = true
            }
        }

        is InstrumentImportState.Error -> {
            enable = true
            showWarningDialog = true

            warnings = when (val error = importState.error) {
                is CanNotImportInstrumentsError -> {
                    error.warnings.map {
                        when (val warningError = it.error) {
                            is InstrumentImportFieldNotFound -> {
                                val field = when (warningError.field) {
                                    InstrumentImportDataFields.STATISTIC_YEAR -> stringResource(Res.string.inst_year)
                                    InstrumentImportDataFields.STATISTIC_MONTH -> stringResource(Res.string.inst_month)
                                    InstrumentImportDataFields.FEDERAL_ENTITY_KEY_CODE -> stringResource(Res.string.inst_federal_entity)
                                    InstrumentImportDataFields.MUNICIPALITY_KEY_CODE -> stringResource(Res.string.inst_municipality)
                                    InstrumentImportDataFields.AGENCY_CONSECUTIVE -> stringResource(Res.string.inst_agency)
                                    InstrumentImportDataFields.STATISTIC_TYPE_KEY_CODE -> stringResource(Res.string.inst_statistic_type)
                                    InstrumentImportDataFields.SAVED_IN_SIRESO -> stringResource(Res.string.inst_in_sireso)
                                    InstrumentImportDataFields.CREATED_AT -> stringResource(Res.string.inst_created_at)
                                    InstrumentImportDataFields.INSTRUMENT_FILE_CONTENT -> stringResource(Res.string.inst_document)
                                }

                                stringResource(
                                    Res.string.inst_catalog_import_missing_field_error_message,
                                    warningError.instrumentName,
                                    field
                                )
                            }

                            else -> stringResource(Res.string.inst_catalog_import_default_error_message)
                        }
                    }
                }

                else -> {
                    listOf(stringResource(Res.string.inst_catalog_import_default_error_message))
                }
            }
        }
    }

    BasicAlertDialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        ),
        onDismissRequest = onDismissRequest,
        content = {
            Surface(
                modifier = Modifier.widthIn(min = 600.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.inst_catalog_import_title),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(16.dp))

                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
                    ) {
                        Row {
                            FilterChip(
                                selected = importType == ImportType.FROM_TEMPLATE,
                                onClick = { importType = ImportType.FROM_TEMPLATE },
                                label = { Text(stringResource(Res.string.inst_catalog_import_from_template)) }
                            )

                            Spacer(Modifier.width(8.dp))

                            FilterChip(
                                selected = importType == ImportType.FROM_V1,
                                onClick = { importType = ImportType.FROM_V1 },
                                label = { Text(stringResource(Res.string.inst_catalog_import_from_v1)) }
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        when (importType) {
                            ImportType.FROM_TEMPLATE -> {
                                ImportFromTemplateContentDialog(
                                    screenModel = screenModel,
                                    enable = enable,
                                    onDismissRequest = onDismissRequest
                                )
                            }

                            ImportType.FROM_V1 -> {
                                ImportFromV1ContentDialog(
                                    screenModel = screenModel,
                                    enable = enable,
                                    onDismissRequest = onDismissRequest
                                )
                            }
                        }
                    }
                }
            }
        }
    )

    if (showWarningDialog) {
        val lazyListState = rememberLazyListState()
        val scrollState = rememberScrollbarAdapter(lazyListState)

        ErrorDialog(
            modifier = Modifier.widthIn(max = 500.dp).heightIn(max = 500.dp),
            text = {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(end = if (scrollState.maxScrollOffset > 0) 8.dp else 0.dp),
                        state = lazyListState
                    ) {
                        items(warnings) { warning ->
                            Text(
                                text = warning,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    VerticalScrollbar(
                        adapter = scrollState,
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                    )
                }
            },
            onConfirmRequest = {
                if (screenModel.importState is InstrumentImportState.Success) {
                    onSuccessImport()
                } else {
                    screenModel.resetImportModal()
                }
            },
            onDismissRequest = {
                if (screenModel.importState is InstrumentImportState.Success) {
                    onSuccessImport()
                } else {
                    screenModel.resetImportModal()
                }
            },
        )
    }
}

@Composable
private fun ImportFromTemplateContentDialog(
    screenModel: InstrumentScreenModel,
    enable: Boolean,
    onDismissRequest: () -> Unit
) {
    var replaceTemplate by remember { mutableStateOf<File?>(null) }
    var showReplaceDialog by remember { mutableStateOf(false) }
    var selectedImportFile by remember { mutableStateOf<File?>(null) }
    val templateFilename = stringResource(Res.string.inst_catalog_import_select_template)
    val fileChooserCsvExtensionDescription = stringResource(Res.string.inst_catalog_import_select_chooser_csv)
    val fileChooser = remember {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileFilter = FileNameExtensionFilter(fileChooserCsvExtensionDescription, "csv")
        }
    }

    Text(stringResource(Res.string.inst_catalog_import_content_csv))

    Spacer(Modifier.height(16.dp))

    Button(
        enabled = enable,
        onClick = {
            fileChooser.apply {
                selectedFile = File(templateFilename)
                val result = showSaveDialog(null)

                if (result == JFileChooser.APPROVE_OPTION) {
                    if (selectedFile.exists()) {
                        replaceTemplate = selectedFile
                        showReplaceDialog = true
                    } else {
                        screenModel.saveTemplate(selectedFile)
                    }
                }

                selectedFile = null
            }
        }
    ) {
        Text(stringResource(Res.string.inst_catalog_import_download_template))
    }

    Spacer(Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            enabled = enable,
            onClick = {
                fileChooser.apply {
                    val result = showOpenDialog(null)
                    selectedImportFile = selectedFile.takeIf { result == JFileChooser.APPROVE_OPTION }
                }
            }
        ) {
            Text(stringResource(Res.string.inst_catalog_import_select_file_csv))
        }

        Spacer(Modifier.width(24.dp))

        selectedImportFile?.let { file ->
            Text(file.name)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            enabled = enable,
            onClick = onDismissRequest
        ) {
            Text(stringResource(Res.string.cancel))
        }

        Spacer(Modifier.width(24.dp))

        TextButton(
            enabled = enable && selectedImportFile != null,
            onClick = {
                screenModel.importInstruments(selectedImportFile!!)
            }
        ) {
            Text(stringResource(Res.string.inst_catalog_import))
        }
    }

    if (showReplaceDialog && replaceTemplate != null) {
        val response = Dialogs.ConfirmDialog("The file exists, overwrite?", "Existing file", Option.YES_NO_CANCEL)

        if (response == Dialogs.Response.YES) {
            screenModel.saveTemplate(replaceTemplate!!)
            replaceTemplate = null
            showReplaceDialog = false
        }
    }
}

@Composable
private fun ImportFromV1ContentDialog(
    screenModel: InstrumentScreenModel,
    enable: Boolean,
    onDismissRequest: () -> Unit
) {
    var selectedImportFile by remember { mutableStateOf<File?>(null) }
    val fileChooserAccdbExtensionDescription = stringResource(Res.string.inst_catalog_import_select_chooser_accdb)
    val fileChooser = remember {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileFilter = FileNameExtensionFilter(fileChooserAccdbExtensionDescription, "accdb")
        }
    }

    Text(stringResource(Res.string.inst_catalog_import_content_accdb))

    Spacer(Modifier.height(16.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            enabled = enable,
            onClick = {
                fileChooser.apply {
                    val result = showOpenDialog(null)
                    selectedImportFile = selectedFile.takeIf { result == JFileChooser.APPROVE_OPTION }
                }
            }
        ) {
            Text(stringResource(Res.string.inst_catalog_import_select_file_accdb))
        }

        Spacer(Modifier.width(24.dp))

        selectedImportFile?.let { file ->
            Text(file.name)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            enabled = enable,
            onClick = onDismissRequest
        ) {
            Text(stringResource(Res.string.cancel))
        }

        Spacer(Modifier.width(24.dp))

        TextButton(
            enabled = enable && selectedImportFile != null,
            onClick = {
                screenModel.importInstrumentsFromV1(selectedImportFile!!)
            }
        ) {
            Text(stringResource(Res.string.inst_catalog_import))
        }
    }
}
