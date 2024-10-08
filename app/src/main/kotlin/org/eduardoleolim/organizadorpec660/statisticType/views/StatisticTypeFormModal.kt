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

package org.eduardoleolim.organizadorpec660.statisticType.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.eduardoleolim.organizadorpec660.statisticType.data.EmptyStatisticTypeDataException
import org.eduardoleolim.organizadorpec660.statisticType.domain.InvalidStatisticTypeKeyCodeError
import org.eduardoleolim.organizadorpec660.statisticType.domain.InvalidStatisticTypeNameError
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeAlreadyExistsError
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeFormState
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeScreenModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatisticTypeScreen.StatisticTypeFormModal(
    screenModel: StatisticTypeScreenModel,
    statisticTypeId: String?,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val title =
        remember { if (statisticTypeId == null) Res.string.st_form_add_title else Res.string.st_form_edit_title }
    var enabled by remember { mutableStateOf(true) }
    var isKeyCodeError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var keyCodeSupportingText: String? by remember { mutableStateOf(null) }
    var nameSupportingText: String? by remember { mutableStateOf(null) }

    val statisticType = screenModel.statisticType
    val keyCode = statisticType.keyCode
    val name = statisticType.name

    DisposableEffect(Unit) {
        screenModel.searchStatisticType(statisticTypeId)

        onDispose {
            screenModel.resetFormModal()
        }
    }

    when (val formState = screenModel.formState) {
        StatisticTypeFormState.Idle -> {
            enabled = true
            isKeyCodeError = false
            isNameError = false
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        StatisticTypeFormState.InProgress -> {
            enabled = false
            isKeyCodeError = false
            isNameError = false
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        StatisticTypeFormState.SuccessCreate -> {
            enabled = true
            onSuccess()
        }

        StatisticTypeFormState.SuccessEdit -> {
            enabled = false
            onSuccess()
        }

        is StatisticTypeFormState.Error -> {
            enabled = true

            when (val error = formState.error) {
                is StatisticTypeAlreadyExistsError -> {
                    keyCodeSupportingText = stringResource(Res.string.st_form_already_exists)
                    isKeyCodeError = true
                }

                is InvalidStatisticTypeKeyCodeError -> {
                    keyCodeSupportingText = stringResource(Res.string.st_form_keycode_format)
                    isKeyCodeError = true
                }

                is InvalidStatisticTypeNameError -> {
                    nameSupportingText = stringResource(Res.string.st_form_name_required)
                    isNameError = true
                }

                is EmptyStatisticTypeDataException -> {
                    if (error.isKeyCodeEmpty) {
                        keyCodeSupportingText = stringResource(Res.string.st_form_keycode_required)
                        isKeyCodeError = true
                    }

                    if (error.isNameEmpty) {
                        nameSupportingText = stringResource(Res.string.st_form_name_required)
                        isNameError = true
                    }
                }

                else -> {
                    println(error)
                }
            }
        }
    }

    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = false
        ),
        onDismissRequest = onDismissRequest,
        title = {
            Text(stringResource(title))
        },
        text = {
            Column {
                OutlinedTextField(
                    label = {
                        Text(stringResource(Res.string.st_keycode))
                    },
                    value = keyCode,
                    onValueChange = {
                        if (Regex("[0-9]{0,3}").matches(it)) {
                            screenModel.updateStatisticTypeKeyCode(it)
                        }
                    },
                    singleLine = true,
                    isError = isKeyCodeError,
                    supportingText = keyCodeSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    },
                    modifier = Modifier.width(300.dp)
                        .onFocusChanged {
                            if (!it.isFocused && keyCode.isNotEmpty()) {
                                screenModel.updateStatisticTypeKeyCode(keyCode)
                            }
                        }
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    label = {
                        Text(stringResource(Res.string.st_name))
                    },
                    value = name,
                    onValueChange = { screenModel.updateStatisticTypeName(it) },
                    singleLine = true,
                    isError = isNameError,
                    supportingText = nameSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    },
                    modifier = Modifier.width(300.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = enabled,
                onClick = { screenModel.saveStatisticType() }
            ) {
                Text(stringResource(Res.string.save))
            }
        },
        dismissButton = {
            TextButton(
                enabled = enabled,
                onClick = onDismissRequest,
            ) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}
