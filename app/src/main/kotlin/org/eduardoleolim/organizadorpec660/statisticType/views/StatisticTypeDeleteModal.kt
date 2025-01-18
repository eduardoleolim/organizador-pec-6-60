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

package org.eduardoleolim.organizadorpec660.statisticType.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.eduardoleolim.organizadorpec660.shared.composables.ErrorDialog
import org.eduardoleolim.organizadorpec660.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.st_delete_text
import org.eduardoleolim.organizadorpec660.shared.resources.st_delete_title
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeDeleteState
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeScreenModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatisticTypeScreen.StatisticTypeDeleteModal(
    screenModel: StatisticTypeScreenModel,
    statisticType: StatisticTypeResponse,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var errorOccurred by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            screenModel.resetDeleteModal()
        }
    }

    when (val deleteState = screenModel.deleteState) {
        StatisticTypeDeleteState.Idle -> {
            errorOccurred = false
            errorMessage = null
        }

        StatisticTypeDeleteState.InProgress -> {
            errorOccurred = false
            errorMessage = null
        }

        StatisticTypeDeleteState.Success -> {
            onSuccess()
        }

        is StatisticTypeDeleteState.Error -> {
            errorOccurred = true
            errorMessage = deleteState.message
        }
    }

    QuestionDialog(
        modifier = Modifier.widthIn(max = 400.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(stringResource(Res.string.st_delete_title))
        },
        text = {
            Text(stringResource(Res.string.st_delete_text, statisticType.name))
        },
        onConfirmRequest = {
            screenModel.deleteStatisticType(statisticType.id)
        },
        onDismissRequest = {
            onDismissRequest()
        }
    )

    if (errorOccurred) {
        ErrorDialog(
            modifier = Modifier.widthIn(max = 400.dp),
            text = {
                errorMessage?.let {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(it)
                    }
                }
            },
            onDismissRequest = { screenModel.resetDeleteModal() },
            onConfirmRequest = onDismissRequest
        )
    }
}
