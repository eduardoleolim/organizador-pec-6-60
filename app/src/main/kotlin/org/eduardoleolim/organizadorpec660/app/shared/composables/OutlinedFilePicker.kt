package org.eduardoleolim.organizadorpec660.app.shared.composables

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

data class NameExtension(val description: String, val extension: String)

@Composable
fun OutlinedFilePicker(
    label: @Composable () -> Unit,
    extensions: List<NameExtension> = emptyList(),
    modifier: Modifier = Modifier,
    onFileSelected: (String) -> Unit
) {
    var filePath by remember { mutableStateOf("") }
    val fileChooser = remember {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            extensions.forEach { extension ->
                addChoosableFileFilter(FileNameExtensionFilter(extension.description, extension.extension))
            }
        }
    }

    OutlinedTextField(
        value = filePath,
        onValueChange = { },
        label = label,
        readOnly = true,
        trailingIcon = {
            IconButton(
                onClick = {
                    val result = fileChooser.showOpenDialog(null)

                    if (result == JFileChooser.APPROVE_OPTION) {
                        fileChooser.selectedFile.absolutePath.let {
                            filePath = it
                            onFileSelected(it)
                        }
                    }
                },
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
