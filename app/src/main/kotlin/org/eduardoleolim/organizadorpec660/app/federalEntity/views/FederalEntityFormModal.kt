package org.eduardoleolim.organizadorpec660.app.federalEntity.views

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
import org.eduardoleolim.organizadorpec660.app.federalEntity.data.EmptyFederalEntityDataException
import org.eduardoleolim.organizadorpec660.app.federalEntity.model.FederalEntityFormState
import org.eduardoleolim.organizadorpec660.app.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.InvalidFederalEntityKeyCodeError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.InvalidFederalEntityNameError
import org.jetbrains.compose.resources.stringResource

@Composable
fun FederalEntityScreen.FederalEntityFormModal(
    screenModel: FederalEntityScreenModel,
    federalEntity: FederalEntityResponse?,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val federalEntityId = remember { federalEntity?.id }
    var keyCode by remember { mutableStateOf(federalEntity?.keyCode ?: "") }
    var name by remember { mutableStateOf(federalEntity?.name ?: "") }

    var enabled by remember { mutableStateOf(true) }
    var isKeyCodeError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var keyCodeSupportingText: String? by remember { mutableStateOf(null) }
    var nameSupportingText: String? by remember { mutableStateOf(null) }

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
            val textTitle = if (federalEntity == null) {
                stringResource(Res.string.fe_form_add_title)
            } else {
                stringResource(Res.string.fe_form_edit_title)
            }

            Text(textTitle)
        },
        text = {
            Column {
                OutlinedTextField(
                    label = { Text(stringResource(Res.string.fe_keycode)) },
                    value = keyCode,
                    onValueChange = {
                        if (Regex("[0-9]{0,2}").matches(it)) {
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
                                keyCode = keyCode.padStart(2, '0')
                            }
                        }
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    label = { Text(stringResource(Res.string.fe_name)) },
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
                    if (federalEntity != null) {
                        screenModel.editFederalEntity(federalEntityId!!, keyCode, name)
                    } else {
                        screenModel.createFederalEntity(keyCode, name)
                    }
                }
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
