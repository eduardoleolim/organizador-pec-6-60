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

package org.eduardoleolim.organizadorpec660.shared.composables

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorDialog(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {
        Text(stringResource(Res.string.dialog_error_title))
    },
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
                modifier = Modifier.width(32.dp)
            )
        },
        title = title,
        text = text,
        confirmButton = {
            TextButton(
                onClick = onConfirmRequest
            ) {
                Text(stringResource(Res.string.accept))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}

@Composable
fun QuestionDialog(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        icon = icon,
        title = title,
        text = text,
        confirmButton = {
            TextButton(
                onClick = onConfirmRequest
            ) {
                Text(stringResource(Res.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.cancel))
            }
        },
    )
}
