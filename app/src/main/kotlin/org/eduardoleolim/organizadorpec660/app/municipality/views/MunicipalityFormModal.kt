package org.eduardoleolim.organizadorpec660.app.municipality.views

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
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.municipality.data.EmptyMunicipalityDataException
import org.eduardoleolim.organizadorpec660.app.municipality.model.FormState
import org.eduardoleolim.organizadorpec660.app.municipality.model.MunicipalityScreenModel
import org.eduardoleolim.organizadorpec660.app.shared.composables.OutlinedSelect
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.municipality.domain.InvalidMunicipalityKeyCodeError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.InvalidMunicipalityNameError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityAlreadyExistsError
import org.jetbrains.compose.resources.stringResource

@Composable
fun MunicipalityScreen.MunicipalityFormModal(
    screenModel: MunicipalityScreenModel,
    municipality: MunicipalityResponse?,
    onDismissRequest: () -> Unit,
    onSuccess: () -> Unit
) {
    val municipalityId by remember { mutableStateOf(municipality?.id) }
    var federalEntity by remember { mutableStateOf(municipality?.federalEntity) }
    var keyCode by remember { mutableStateOf(municipality?.keyCode ?: "") }
    var name by remember { mutableStateOf(municipality?.name ?: "") }

    val titleResource = remember {
        if (municipality == null) Res.string.mun_form_add_title else Res.string.mun_form_edit_title
    }
    var enabled by remember { mutableStateOf(true) }
    var isFederalEntityError by remember { mutableStateOf(false) }
    var isKeyCodeError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var federalEntitySupportingText: String? by remember { mutableStateOf(null) }
    var keyCodeSupportingText: String? by remember { mutableStateOf(null) }
    var nameSupportingText: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        screenModel.searchAllFederalEntities()
    }

    when (val formState = screenModel.formState) {
        FormState.Idle -> {
            enabled = true
            isFederalEntityError = false
            isKeyCodeError = false
            isNameError = false
            federalEntitySupportingText = null
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        FormState.InProgress -> {
            enabled = false
            isFederalEntityError = false
            isKeyCodeError = false
            isNameError = false
            federalEntitySupportingText = null
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        FormState.SuccessCreate -> {
            enabled = true
            onSuccess()
        }

        FormState.SuccessEdit -> {
            enabled = true
            onSuccess()
        }

        is FormState.Error -> {
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
            Text(stringResource(titleResource))
        },
        text = {
            Column {
                OutlinedSelect(
                    items = screenModel.federalEntities,
                    onValueSelected = { _, item -> federalEntity = item },
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

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    enabled = true,
                    label = {
                        Text(stringResource(Res.string.mun_keycode))
                    },
                    value = keyCode,
                    onValueChange = {
                        if (Regex("[0-9]{0,3}").matches(it)) {
                            keyCode = it
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
                                keyCode = keyCode.padStart(3, '0')
                            }
                        }
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    enabled = true,
                    label = {
                        Text(stringResource(Res.string.mun_name))
                    },
                    value = name,
                    onValueChange = { name = it.uppercase() },
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
                onClick = {
                    if (municipalityId == null) {
                        screenModel.createMunicipality(keyCode, name, federalEntity?.id)
                    } else {
                        screenModel.editMunicipality(municipalityId!!, keyCode, name, federalEntity?.id)
                    }
                }
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
