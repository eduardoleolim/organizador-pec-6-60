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

package org.eduardoleolim.organizadorpec660.municipality.views

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
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.municipality.data.EmptyMunicipalityDataException
import org.eduardoleolim.organizadorpec660.municipality.domain.InvalidMunicipalityKeyCodeError
import org.eduardoleolim.organizadorpec660.municipality.domain.InvalidMunicipalityNameError
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.municipality.model.MunicipalityFormState
import org.eduardoleolim.organizadorpec660.municipality.model.MunicipalityScreenModel
import org.eduardoleolim.organizadorpec660.shared.composables.OutlinedSelect
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun MunicipalityScreen.MunicipalityFormModal(
    screenModel: MunicipalityScreenModel,
    municipalityId: String?,
    onDismissRequest: () -> Unit,
    onSuccess: () -> Unit
) {
    val title =
        remember { if (municipalityId == null) Res.string.mun_form_add_title else Res.string.mun_form_edit_title }
    var enabled by remember { mutableStateOf(true) }
    var isFederalEntityError by remember { mutableStateOf(false) }
    var isKeyCodeError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var federalEntitySupportingText: String? by remember { mutableStateOf(null) }
    var keyCodeSupportingText: String? by remember { mutableStateOf(null) }
    var nameSupportingText: String? by remember { mutableStateOf(null) }

    val federalEntities = screenModel.federalEntities
    val municipality = screenModel.municipality
    val federalEntity = municipality.federalEntity
    val keyCode = municipality.keyCode
    val name = municipality.name
    val federalEntityIndex = federalEntities.indexOfFirst { it.id == federalEntity?.id }.takeIf { it != -1 }

    DisposableEffect(Unit) {
        screenModel.searchAllFederalEntities()
        screenModel.searchMunicipality(municipalityId)

        onDispose {
            screenModel.resetFormModal()
        }
    }

    when (val formState = screenModel.formState) {
        MunicipalityFormState.Idle -> {
            enabled = true
            isFederalEntityError = false
            isKeyCodeError = false
            isNameError = false
            federalEntitySupportingText = null
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        MunicipalityFormState.InProgress -> {
            enabled = false
            isFederalEntityError = false
            isKeyCodeError = false
            isNameError = false
            federalEntitySupportingText = null
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        MunicipalityFormState.SuccessCreate -> {
            enabled = true
            onSuccess()
        }

        MunicipalityFormState.SuccessEdit -> {
            enabled = true
            onSuccess()
        }

        is MunicipalityFormState.Error -> {
            enabled = true

            when (val error = formState.error) {
                is FederalEntityNotFoundError -> {
                    federalEntitySupportingText = stringResource(Res.string.mun_form_federal_entity_does_not_exist)
                    isFederalEntityError = true
                }

                is MunicipalityAlreadyExistsError -> {
                    val message = stringResource(Res.string.mun_form_already_exists)
                    federalEntitySupportingText = message
                    keyCodeSupportingText = message
                    isFederalEntityError = true
                    isKeyCodeError = true
                }

                is InvalidMunicipalityKeyCodeError -> {
                    keyCodeSupportingText = stringResource(Res.string.mun_form_keycode_format)
                    isKeyCodeError = true
                }

                is InvalidMunicipalityNameError -> {
                    nameSupportingText = stringResource(Res.string.mun_form_name_format)
                    isNameError = true
                }

                is EmptyMunicipalityDataException -> {
                    if (error.isFederalEntityEmpty) {
                        federalEntitySupportingText = stringResource(Res.string.mun_form_federal_entity_required)
                        isFederalEntityError = true
                    }
                    if (error.isKeyCodeEmpty) {
                        keyCodeSupportingText = stringResource(Res.string.mun_form_keycode_required)
                        isKeyCodeError = true
                    }
                    if (error.isNameEmpty) {
                        nameSupportingText = stringResource(Res.string.mun_form_name_required)
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
                OutlinedSelect(
                    items = federalEntities,
                    index = federalEntityIndex,
                    onValueSelected = { _, federalEntity ->
                        screenModel.updateMunicipalityFederalEntity(federalEntity)
                    },
                    visualTransformation = { "${it.keyCode} - ${it.name}" },
                    label = {
                        Text(stringResource(Res.string.mun_federal_entity))
                    },
                    isError = isFederalEntityError,
                    supportingText = federalEntitySupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    },
                    modifier = Modifier.width(300.dp)
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    enabled = true,
                    label = {
                        Text(stringResource(Res.string.mun_keycode))
                    },
                    value = keyCode,
                    onValueChange = {
                        if (Regex("[0-9]{0,3}").matches(it)) {
                            screenModel.updateMunicipalityKeyCode(it)
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
                                screenModel.updateMunicipalityKeyCode(keyCode)
                            }
                        }
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    enabled = true,
                    label = {
                        Text(stringResource(Res.string.mun_name))
                    },
                    value = name,
                    onValueChange = { screenModel.updateMunicipalityName(it.uppercase()) },
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
                onClick = { screenModel.saveMunicipality() }
            ) {
                Text(stringResource(Res.string.save))
            }
        },
        dismissButton = {
            TextButton(
                enabled = enabled,
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}
