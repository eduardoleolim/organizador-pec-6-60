package org.eduardoleolim.organizadorpec660.app.federalEntity.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.eduardoleolim.organizadorpec660.app.federalEntity.data.EmptyFederalEntityDataException
import org.eduardoleolim.organizadorpec660.app.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorpec660.app.federalEntity.model.FormState
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.InvalidFederalEntityKeyCodeError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.InvalidFederalEntityNameError

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

    when (val formState = screenModel.formState.value) {
        FormState.Idle -> {
            enabled = true
            isKeyCodeError = false
            isNameError = false
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        FormState.InProgress -> {
            enabled = false
            isKeyCodeError = false
            isNameError = false
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
                is InvalidFederalEntityKeyCodeError -> {
                    keyCodeSupportingText = "La clave debe ser un número de dos dígitos"
                    isKeyCodeError = true
                }

                is InvalidFederalEntityNameError -> {
                    nameSupportingText = "El nombre no puede estar vacío"
                    isNameError = true
                }

                is FederalEntityAlreadyExistsError -> {
                    keyCodeSupportingText = "Ya existe una entidad federativa con esa clave"
                    isKeyCodeError = true
                }

                is EmptyFederalEntityDataException -> {
                    if (error.isKeyCodeEmpty) {
                        keyCodeSupportingText = "La clave es requerida."
                        isKeyCodeError = true
                    }
                    if (error.isNameEmpty) {
                        nameSupportingText = "El nombre es requerido"
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
            Text(federalEntity?.let { "Editar entidad federativa" } ?: "Agregar entidad federativa")
        },
        text = {
            Column {
                OutlinedTextField(
                    label = { Text("Clave") },
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
                    modifier = Modifier.onFocusChanged {
                        if (!it.isFocused && keyCode.isNotEmpty()) {
                            keyCode = keyCode.padStart(2, '0')
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    label = { Text("Nombre") },
                    value = name,
                    onValueChange = { name = it.uppercase() },
                    singleLine = true,
                    isError = isNameError,
                    supportingText = nameSupportingText?.let { message ->
                        { Text(text = message, color = MaterialTheme.colorScheme.error) }
                    }
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
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                enabled = enabled,
                onClick = onDismissRequest,
            ) {
                Text("Cancelar")
            }
        }
    )
}
