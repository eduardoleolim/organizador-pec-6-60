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

package org.eduardoleolim.organizadorpec660.agency.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.eduardoleolim.organizadorpec660.agency.data.EmptyAgencyDataException
import org.eduardoleolim.organizadorpec660.agency.model.AgencyFormState
import org.eduardoleolim.organizadorpec660.agency.model.AgencyScreenModel
import org.eduardoleolim.organizadorpec660.shared.composables.OutlinedSelect
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AgencyScreen.AgencyFormModal(
    screenModel: AgencyScreenModel,
    agencyId: String?,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val title = remember { if (agencyId == null) Res.string.ag_form_add_title else Res.string.ag_form_edit_title }
    var enabled by remember { mutableStateOf(true) }
    var isNameError by remember { mutableStateOf(false) }
    var isConsecutiveError by remember { mutableStateOf(false) }
    var isFederalEntityError by remember { mutableStateOf(false) }
    var isMunicipalityError by remember { mutableStateOf(false) }
    var isStatisticTypeError by remember { mutableStateOf(false) }
    var nameSupportingText by mutableStateOf<String?>(null)
    var consecutiveSupportingText by mutableStateOf<String?>(null)
    var federalEntitySupportingText by remember { mutableStateOf<String?>(null) }
    var municipalitySupportingText by remember { mutableStateOf<String?>(null) }
    var statisticTypeSupportingText by remember { mutableStateOf<String?>(null) }
    val formScrollState = rememberScrollState()

    val federalEntities = screenModel.federalEntities
    val municipalities = screenModel.municipalities
    val agency = screenModel.agency
    val federalEntity = agency.federalEntity
    val municipality = agency.municipality
    val federalEntityIndex = federalEntities.indexOfFirst { it.id == federalEntity?.id }.takeIf { it != -1 }
    val municipalityIndex = municipalities.indexOfFirst { it.id == municipality?.id }.takeIf { it != -1 }

    DisposableEffect(Unit) {
        screenModel.searchAllFederalEntities()
        screenModel.searchAllStatisticTypes()
        screenModel.searchAgency(agencyId)

        onDispose {
            screenModel.resetFormModal()
        }
    }

    LaunchedEffect(federalEntity) {
        screenModel.searchMunicipalities(federalEntity?.id)
    }

    when (val formState = screenModel.formState) {
        AgencyFormState.Idle -> {
            enabled = true
            isNameError = false
            isConsecutiveError = false
            isFederalEntityError = false
            isMunicipalityError = false
            isStatisticTypeError = false
            nameSupportingText = null
            consecutiveSupportingText = null
            federalEntitySupportingText = null
            municipalitySupportingText = null
            statisticTypeSupportingText = null
        }

        AgencyFormState.InProgress -> {
            enabled = false
            isNameError = false
            isConsecutiveError = false
            isFederalEntityError = false
            isMunicipalityError = false
            isStatisticTypeError = false
            nameSupportingText = null
            consecutiveSupportingText = null
            federalEntitySupportingText = null
            municipalitySupportingText = null
            statisticTypeSupportingText = null
        }

        AgencyFormState.SuccessCreate -> {
            enabled = true
            onSuccess()
        }

        AgencyFormState.SuccessEdit -> {
            enabled = true
            onSuccess()
        }

        is AgencyFormState.Error -> {
            enabled = true

            when (val error = formState.error) {

                is EmptyAgencyDataException -> {
                    if (error.isNameEmpty) {
                        nameSupportingText = stringResource(Res.string.ag_form_name_required)
                        isNameError = true
                    }

                    if (error.isConsecutiveEmpty) {
                        consecutiveSupportingText = stringResource(Res.string.ag_form_consecutive_required)
                        isConsecutiveError = true
                    }

                    if (error.isMunicipalityEmpty) {
                        municipalitySupportingText = stringResource(Res.string.ag_form_municipality_required)
                        isMunicipalityError = true
                    }

                    if (error.isStatisticTypesEmpty) {
                        statisticTypeSupportingText = stringResource(Res.string.ag_form_statistic_type_required)
                        isStatisticTypeError = true
                    }
                }

                else -> {
                    println("Error: ${error.message}")
                }
            }
        }
    }

    AlertDialog(
        modifier = Modifier.fillMaxHeight(0.8f),
        properties = DialogProperties(
            dismissOnClickOutside = false
        ),
        onDismissRequest = onDismissRequest,
        title = {
            Text(stringResource(title))
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(formScrollState)
            ) {
                Text(
                    text = stringResource(Res.string.ag_form_general),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    OutlinedTextField(
                        label = {
                            Text(stringResource(Res.string.ag_name))
                        },
                        value = agency.name,
                        onValueChange = {
                            screenModel.updateAgencyName(it.uppercase())
                        },
                        singleLine = true,
                        isError = isNameError,
                        supportingText = nameSupportingText?.let { message ->
                            { Text(text = message, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.width(240.dp)
                    )

                    Spacer(Modifier.width(32.dp))

                    OutlinedTextField(
                        label = {
                            Text(stringResource(Res.string.ag_consecutive))
                        },
                        value = agency.consecutive,
                        onValueChange = {
                            if (it.isBlank()) {
                                screenModel.updateAgencyConsecutive("")
                            } else if (Regex("^\\d{0,4}$").matches(it)) {
                                screenModel.updateAgencyConsecutive(it)
                            }
                        },
                        singleLine = true,
                        isError = isConsecutiveError,
                        supportingText = consecutiveSupportingText?.let { message ->
                            { Text(text = message, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier
                            .width(240.dp)
                            .onFocusChanged {
                                if (!it.isFocused && agency.consecutive.isNotEmpty()) {
                                    screenModel.updateAgencyConsecutive(agency.consecutive.padStart(4, '0'))
                                }
                            }
                    )
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    text = stringResource(Res.string.ag_form_municipality),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    OutlinedSelect(
                        items = federalEntities,
                        index = federalEntityIndex,
                        onValueSelected = { _, item ->
                            screenModel.updateAgencyFederalEntity(item)
                        },
                        visualTransformation = { "${it.keyCode} - ${it.name}" },
                        label = {
                            Text(stringResource(Res.string.mun_federal_entity))
                        },
                        isError = isFederalEntityError,
                        supportingText = federalEntitySupportingText?.let { message ->
                            { Text(text = message, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.width(240.dp)
                    )

                    Spacer(Modifier.width(32.dp))

                    OutlinedSelect(
                        enabled = federalEntity != null,
                        items = municipalities,
                        index = municipalityIndex,
                        onValueSelected = { _, item ->
                            screenModel.updateAgencyMunicipality(item)
                        },
                        visualTransformation = { "${it.keyCode} - ${it.name}" },
                        label = {
                            Text(stringResource(Res.string.ag_form_municipality))
                        },
                        isError = isMunicipalityError,
                        supportingText = municipalitySupportingText?.let { message ->
                            { Text(text = message, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.width(240.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.ag_form_statistic_types),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.width(16.dp))

                    statisticTypeSupportingText?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Column {
                    screenModel.statisticTypes.forEach { statisticType ->
                        val isChecked = agency.statisticTypes.firstOrNull { it.id == statisticType.id } != null
                        val checkboxColors = if (isStatisticTypeError) {
                            CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.error,
                                uncheckedColor = MaterialTheme.colorScheme.error
                            )
                        } else {
                            CheckboxDefaults.colors()
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .toggleable(
                                    value = isChecked,
                                    onValueChange = { isSelected ->
                                        if (isSelected) {
                                            screenModel.addAgencyStatisticType(statisticType)
                                        } else {
                                            screenModel.removeAgencyStatisticType(statisticType)
                                        }
                                    },
                                    role = Role.Checkbox
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = null,
                                colors = checkboxColors
                            )
                            Text(
                                text = "${statisticType.keyCode} - ${statisticType.name}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = enabled,
                onClick = { screenModel.saveAgency() }
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
