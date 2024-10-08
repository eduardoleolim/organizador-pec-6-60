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

package org.eduardoleolim.organizadorpec660.federalEntity.views

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
import org.eduardoleolim.organizadorpec660.federalEntity.data.EmptyFederalEntityDataException
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.federalEntity.domain.InvalidFederalEntityKeyCodeError
import org.eduardoleolim.organizadorpec660.federalEntity.domain.InvalidFederalEntityNameError
import org.eduardoleolim.organizadorpec660.federalEntity.model.FederalEntityFormState
import org.eduardoleolim.organizadorpec660.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FederalEntityScreen.FederalEntityFormModal(
    screenModel: FederalEntityScreenModel,
    federalEntityId: String?,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val title =
        remember { if (federalEntityId == null) Res.string.fe_form_add_title else Res.string.fe_form_edit_title }
    var enabled by remember { mutableStateOf(true) }
    var isKeyCodeError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var keyCodeSupportingText: String? by remember { mutableStateOf(null) }
    var nameSupportingText: String? by remember { mutableStateOf(null) }

    val federalEntity = screenModel.federalEntity
    val keyCode = federalEntity.keyCode
    val name = federalEntity.name

    DisposableEffect(Unit) {
        screenModel.searchFederalEntity(federalEntityId)

        onDispose {
            screenModel.resetFormModal()
        }
    }

    when (val formState = screenModel.formState) {
        FederalEntityFormState.Idle -> {
            enabled = true
            isKeyCodeError = false
            isNameError = false
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        FederalEntityFormState.InProgress -> {
            enabled = false
            isKeyCodeError = false
            isNameError = false
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        FederalEntityFormState.SuccessCreate -> {
            enabled = true
            onSuccess()
        }

        FederalEntityFormState.SuccessEdit -> {
            enabled = true
            onSuccess()
        }

        is FederalEntityFormState.Error -> {
            enabled = true

            when (val error = formState.error) {
                is InvalidFederalEntityKeyCodeError -> {
                    keyCodeSupportingText = stringResource(Res.string.fe_form_keycode_format)
                    isKeyCodeError = true
                }

                is InvalidFederalEntityNameError -> {
                    nameSupportingText = stringResource(Res.string.fe_form_name_format)
                    isNameError = true
                }

                is FederalEntityAlreadyExistsError -> {
                    keyCodeSupportingText = stringResource(Res.string.fe_form_already_exists)
                    isKeyCodeError = true
                }

                is EmptyFederalEntityDataException -> {
                    if (error.isKeyCodeEmpty) {
                        keyCodeSupportingText = stringResource(Res.string.fe_form_keycode_required)
                        isKeyCodeError = true
                    }
                    if (error.isNameEmpty) {
                        nameSupportingText = stringResource(Res.string.fe_form_name_required)
                        isNameError = true
                    }
                }

                else -> {
                    println("Error: ${error.message}")
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
                        Text(stringResource(Res.string.fe_keycode))
                    },
                    value = keyCode,
                    onValueChange = {
                        if (Regex("[0-9]{0,2}").matches(it)) {
                            screenModel.updateFederalEntityKeyCode(it)
                        }
                    },
                    singleLine = true,
                    isError = isKeyCodeError,
                    supportingText = keyCodeSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .onFocusChanged {
                            if (!it.isFocused && keyCode.isNotEmpty()) {
                                screenModel.updateFederalEntityKeyCode(keyCode.padStart(2, '0'))
                            }
                        }
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    label = {
                        Text(stringResource(Res.string.fe_name))
                    },
                    value = name,
                    onValueChange = { screenModel.updateFederalEntityName(it.uppercase()) },
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
                onClick = { screenModel.saveFederalEntity() }
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
