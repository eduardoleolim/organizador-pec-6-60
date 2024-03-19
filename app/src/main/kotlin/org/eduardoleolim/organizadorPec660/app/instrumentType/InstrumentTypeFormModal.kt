package org.eduardoleolim.organizadorPec660.app.instrumentType

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogProperties
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypeResponse
import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentTypeAlreadyExistsError
import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InvalidInstrumentTypeNameError

@Composable
fun InstrumentTypeScreen.InstrumentTypeFormModal(
    screenModel: InstrumentTypeScreenModel,
    instrumentType: InstrumentTypeResponse?,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val instrumentTypeId = remember { instrumentType?.id }
    var name by remember { mutableStateOf(instrumentType?.name ?: "") }

    var enabled by remember { mutableStateOf(true) }
    var isNameError by remember { mutableStateOf(false) }
    var nameSupportingText: String? by remember { mutableStateOf(null) }

    when (val formState = screenModel.formState.value) {
        FormState.Idle -> {
            enabled = true
            isNameError = false
            nameSupportingText = null
        }

        FormState.InProgress -> {
            enabled = false
            isNameError = false
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
                is InvalidInstrumentTypeNameError -> {
                    isNameError = true
                    nameSupportingText = "El nombre no puede estar vacío"
                }

                is InstrumentTypeAlreadyExistsError -> {
                    isNameError = true
                    nameSupportingText = "Ya existe un tipo de instrumento con ese nombre"
                }

                is EmptyInstrumentTypeDataException -> {
                    isNameError = true
                    nameSupportingText = "El nombre es requerido"
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
            Text(instrumentType?.let { "Editar tipo de instrumento" } ?: "Agregar tipo de instrumento")
        },
        text = {
            Column {
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
                    if (instrumentType != null) {
                        screenModel.editInstrumentType(instrumentTypeId!!, name)
                    } else {
                        screenModel.createInstrumentType(name)
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
