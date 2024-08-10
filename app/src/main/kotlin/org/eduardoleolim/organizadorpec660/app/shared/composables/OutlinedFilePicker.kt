package org.eduardoleolim.organizadorpec660.app.shared.composables

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object ResetFilePickerInteraction : Interaction

data class NameExtension(val description: String, val extension: String, val isDefault: Boolean = false)

@Composable
fun OutlinedFilePicker(
    value: String? = null,
    enabled: Boolean = true,
    label: @Composable () -> Unit,
    extensions: List<NameExtension> = emptyList(),
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    onFileSelected: (String) -> Unit
) {
    var filePath by remember { mutableStateOf("") }
    val fileChooser = remember {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            extensions.forEach { extension ->
                val filter = FileNameExtensionFilter(extension.description, extension.extension)
                addChoosableFileFilter(filter)
                if (fileFilter !== null && extension.isDefault) {
                    fileFilter = filter
                }
            }
        }
    }

    LaunchedEffect(value) {
        if (value != null) {
            filePath = value
        }
    }

    LaunchedEffect(interactionSource) {
        interactionSource?.interactions?.collect { interaction ->
            if (interaction is ResetFilePickerInteraction) {
                filePath = ""
            }
        }
    }

    OutlinedTextField(
        enabled = enabled,
        value = filePath,
        onValueChange = { },
        label = label,
        readOnly = true,
        singleLine = true,
        trailingIcon = {
            IconButton(
                enabled = enabled,
                onClick = {
                    val result = fileChooser.showOpenDialog(null)

                    if (result == JFileChooser.APPROVE_OPTION) {
                        fileChooser.selectedFile.absolutePath.let {
                            filePath = it
                            onFileSelected(it)
                        }
                    }
                },
                interactionSource = interactionSource,
                modifier = Modifier.pointerHoverIcon(PointerIcon.Default)
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = "Select file"
                )
            }
        },
        modifier = Modifier.then(modifier)
    )
}
