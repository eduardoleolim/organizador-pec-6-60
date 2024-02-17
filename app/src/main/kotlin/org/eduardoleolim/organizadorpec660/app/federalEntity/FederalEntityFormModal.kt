package org.eduardoleolim.organizadorpec660.app.federalEntity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.InvalidFederalEntityKeyCodeError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.InvalidFederalEntityNameError

@Composable
fun FederalEntityScreen.FederalEntityFormModal(
    screenModel: FederalEntityScreenModel,
    selectedFederalEntity: FederalEntityResponse?,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val federalEntityId = remember { selectedFederalEntity?.id }
    var keyCode by remember { mutableStateOf(selectedFederalEntity?.keyCode ?: "") }
    var name by remember { mutableStateOf(selectedFederalEntity?.name ?: "") }

    var enabled by remember { mutableStateOf(true) }
    var isKeyCodeInvalid by remember { mutableStateOf(false) }
    var keyCodeErrorMessage by remember { mutableStateOf("") }
    var isNameInvalid by remember { mutableStateOf(false) }
    var nameErrorMessage by remember { mutableStateOf("") }

    fun handleError(error: Throwable) {
        when (error) {
            is InvalidFederalEntityKeyCodeError -> {
                keyCodeErrorMessage = "La clave debe ser un número de dos dígitos"
                isKeyCodeInvalid = true
            }

            is InvalidFederalEntityNameError -> {
                nameErrorMessage = "El nombre no puede estar vacío"
                isNameInvalid = true
            }

            is FederalEntityAlreadyExistsError -> {
                keyCodeErrorMessage = "Ya existe una entidad federativa con esa clave"
                isKeyCodeInvalid = true
            }
        }
    }

    AlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        onDismissRequest = onDismissRequest,
        title = {
            Text(selectedFederalEntity?.let { "Editar entidad federativa" } ?: "Agregar entidad federativa")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = keyCode,
                    onValueChange = {
                        if (Regex("[0-9]{0,2}").matches(it)) {
                            keyCode = it
                        }
                    },
                    label = { Text("Clave") },
                    isError = isKeyCodeInvalid,
                    supportingText = {
                        if (isKeyCodeInvalid) {
                            Text(
                                text = keyCodeErrorMessage,
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            if (!it.isFocused && keyCode.isNotEmpty()) {
                                keyCode = keyCode.padStart(2, '0')
                            }
                        },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.uppercase() },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isNameInvalid,
                    supportingText = {
                        if (isNameInvalid) {
                            Text(
                                text = nameErrorMessage,
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = enabled,
                onClick = {
                    enabled = false
                    isKeyCodeInvalid = false
                    isNameInvalid = false
                    nameErrorMessage = ""
                    keyCodeErrorMessage = ""

                    if (selectedFederalEntity != null) {
                        screenModel.editFederalEntity(federalEntityId!!, keyCode, name) { result ->
                            result.fold(
                                onSuccess = {
                                    onSuccess()
                                },
                                onFailure = { error ->
                                    handleError(error)
                                }
                            )
                            enabled = true
                        }
                    } else {
                        screenModel.createFederalEntity(keyCode, name) { result ->
                            result.fold(
                                onSuccess = {
                                    onSuccess()
                                },
                                onFailure = { error ->
                                    handleError(error)
                                }
                            )
                            enabled = true
                        }
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
