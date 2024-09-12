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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eduardoleolim.organizadorpec660.federalEntity.data.FederalEntityImportException
import org.eduardoleolim.organizadorpec660.federalEntity.model.FederalEntityImportState
import org.eduardoleolim.organizadorpec660.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.shared.composables.ErrorDialog
import org.eduardoleolim.organizadorpec660.shared.dialogs.Dialogs
import org.eduardoleolim.organizadorpec660.shared.dialogs.Dialogs.Option
import org.jetbrains.compose.resources.stringResource
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun FederalEntityScreen.FederalEntityImportExportModal(
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onExportClick) {
                Text(stringResource(Res.string.fe_catalog_export))
            }
        },
        dismissButton = {
            TextButton(onClick = onImportClick) {
                Text(stringResource(Res.string.fe_catalog_import))
            }
        },
        title = {
            Text(stringResource(Res.string.fe_catalog_title))
        },
        text = {
            Text(stringResource(Res.string.fe_catalog_content))
        }
    )
}

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
                is FederalEntityImportException -> {
                    error.warnings.mapNotNull { it.message }
                }

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

        LaunchedEffect(response) {
            withContext(Dispatchers.IO) {
                if (response == Dialogs.Response.YES) {
                    screenModel.saveTemplate(replaceTemplate!!)
                    replaceTemplate = null
                    showReplaceDialog = false
                }
            }
        }
    }

    if (showWarningDialog) {
        ErrorDialog(
            onDismissRequest = {
                if (screenModel.importState is FederalEntityImportState.Success) {
                    onSuccessImport()
                } else {
                    screenModel.resetImportModal()
                }
            },
            onConfirmRequest = {
                if (screenModel.importState is FederalEntityImportState.Success) {
                    onSuccessImport()
                } else {
                    screenModel.resetImportModal()
                }
            },
            title = {
                LazyColumn {
                    items(warnings) { warning ->
                        Text(warning)
                    }
                }
            },
            text = {
                Text("Error")
            }
        )
    }
}
