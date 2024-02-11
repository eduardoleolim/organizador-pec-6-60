package org.eduardoleolim.organizadorpec660.app.federalEntity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse

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

    AlertDialog(
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
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedFederalEntity != null) {
                        screenModel.editFederalEntity(federalEntityId!!, keyCode, name) { result ->
                            result.fold(
                                onSuccess = {
                                    onSuccess()
                                },
                                onFailure = {
                                    println(it.localizedMessage)
                                }
                            )
                        }
                    } else {
                        screenModel.createFederalEntity(keyCode, name) { result ->
                            result.fold(
                                onSuccess = {
                                    onSuccess()
                                },
                                onFailure = {
                                    println(it.localizedMessage)
                                }
                            )
                        }
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text("Cancelar")
            }
        }
    )
}
