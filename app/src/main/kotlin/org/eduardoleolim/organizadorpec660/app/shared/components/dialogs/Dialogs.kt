package org.eduardoleolim.organizadorpec660.app.shared.components.dialogs

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ErrorDialog(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.width(24.dp)
            )
        },
        title = title,
        text = text,
        confirmButton = {
            TextButton(
                onClick = onConfirmRequest
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun QuestionDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.QuestionMark,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = "Error",
                modifier = Modifier.width(24.dp)
            )
        },
        title = title,
        text = text,
        confirmButton = {
            TextButton(
                onClick = onConfirmRequest
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancelar")
            }
        },
    )
}
