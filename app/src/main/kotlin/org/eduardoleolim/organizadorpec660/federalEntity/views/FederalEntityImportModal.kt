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

package org.eduardoleolim.organizadorpec660.federalEntity.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.eduardoleolim.organizadorpec660.federalEntity.model.FederalEntityImportState
import org.eduardoleolim.organizadorpec660.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorpec660.shared.composables.ErrorDialog
import org.eduardoleolim.organizadorpec660.shared.dialogs.Dialogs
import org.eduardoleolim.organizadorpec660.shared.dialogs.Dialogs.Option
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun FederalEntityScreen.FederalEntityImportModal(
    screenModel: FederalEntityScreenModel,
    onSuccessImport: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var replaceTemplate by remember { mutableStateOf<File?>(null) }
    var selectedImportFile by remember { mutableStateOf<File?>(null) }
    val templateFilename = stringResource(Res.string.fe_catalog_import_select_template)
    val fileChooserExtensionDescription = stringResource(Res.string.fe_catalog_import_select_chooser)
    val fileChooser = remember {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileFilter = FileNameExtensionFilter(fileChooserExtensionDescription, "csv")
        }
    }

    var enable by remember { mutableStateOf(true) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var showReplaceDialog by remember { mutableStateOf(false) }
    var warnings by remember { mutableStateOf(emptyList<String>()) }

    DisposableEffect(Unit) {
        onDispose {
            screenModel.resetImportModal()
        }
    }

    when (val importState = screenModel.importState) {
        FederalEntityImportState.Idle -> {
            enable = true
            showWarningDialog = false
        }

        FederalEntityImportState.InProgress -> {
            enable = false
            showWarningDialog = false
        }

        is FederalEntityImportState.Success -> {
            if (importState.warnings.isEmpty()) {
                onSuccessImport()
            } else {
                warnings = importState.warnings.mapNotNull { it.message }
                showWarningDialog = true
            }
        }

        is FederalEntityImportState.Error -> {
            enable = true
            showWarningDialog = true

            warnings = when (val error = importState.error) {
                else -> {
                    listOf("error")
                }
            }
        }
    }

    AlertDialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        ),
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = enable && selectedImportFile != null,
                onClick = {
                    screenModel.importFederalEntities(selectedImportFile!!)
                }
            ) {
                Text(stringResource(Res.string.fe_catalog_import))
            }
        },
        dismissButton = {
            TextButton(
                enabled = enable,
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.cancel))
            }
        },
        title = {
            Text(stringResource(Res.string.fe_catalog_import_title))
        },
        text = {
            Column {
                Text(stringResource(Res.string.fe_catalog_import_content))

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
                    Text(stringResource(Res.string.fe_catalog_import_download_template))
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
                        Text(stringResource(Res.string.fe_catalog_import_select_file))
                    }

                    Spacer(Modifier.width(24.dp))

                    selectedImportFile?.let { file ->
                        Text(file.name)
                    }
                }
            }
        }
    )

    if (showReplaceDialog && replaceTemplate != null) {
        val response = Dialogs.ConfirmDialog("The file exists, overwrite?", "Existing file", Option.YES_NO_CANCEL)

        if (response == Dialogs.Response.YES) {
            screenModel.saveTemplate(replaceTemplate!!)
            replaceTemplate = null
            showReplaceDialog = false
        }
    }

    if (showWarningDialog) {
        ErrorDialog(
            text = {
                LazyColumn {
                    items(warnings) { warning ->
                        Text(warning)
                    }
                }
            },
            onConfirmRequest = {
                if (screenModel.importState is FederalEntityImportState.Success) {
                    onSuccessImport()
                } else {
                    screenModel.resetImportModal()
                }
            },
            onDismissRequest = {
                if (screenModel.importState is FederalEntityImportState.Success) {
                    onSuccessImport()
                } else {
                    screenModel.resetImportModal()
                }
            },
        )
    }
}
