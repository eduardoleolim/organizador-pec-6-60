package org.eduardoleolim.organizadorpec660.app.statisticType.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.eduardoleolim.organizadorpec660.app.statisticType.model.FormState
import org.eduardoleolim.organizadorpec660.app.statisticType.model.StatisticTypeScreenModel
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypeResponse

@Composable
fun StatisticTypeScreen.StatisticTypeFormModal(
    screenModel: StatisticTypeScreenModel,
    statisticType: StatisticTypeResponse?,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val statisticTypeId = remember { statisticType?.id }
    var keyCode by remember { mutableStateOf(statisticType?.keyCode ?: "") }
    var name by remember { mutableStateOf(statisticType?.name ?: "") }

    var enabled by remember { mutableStateOf(true) }
    var isKeyCodeError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var isInstrumentTypeIdsError by remember { mutableStateOf(false) }
    var keyCodeSupportingText: String? by remember { mutableStateOf(null) }
    var nameSupportingText: String? by remember { mutableStateOf(null) }
    var instrumentTypeIdsSupportingText: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        screenModel.searchAllInstrumentTypes()
    }

    when (val formState = screenModel.formState.value) {
        FormState.Idle -> {
            enabled = true
            isKeyCodeError = false
            isNameError = false
            isInstrumentTypeIdsError = false
            keyCodeSupportingText = null
            nameSupportingText = null
            instrumentTypeIdsSupportingText = null
        }

        FormState.InProgress -> {
            enabled = false
            isKeyCodeError = false
            isNameError = false
            isInstrumentTypeIdsError = false
            keyCodeSupportingText = null
            nameSupportingText = null
            instrumentTypeIdsSupportingText = null
        }

        FormState.SuccessCreate -> {
            enabled = true
            onSuccess()
        }

        FormState.SuccessEdit -> {
            enabled = false
            onSuccess()
        }

        is FormState.Error -> {
            enabled = true

            when (val error = formState.error) {
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
            Text(statisticType?.let { "Editar tipo de estadística" } ?: "Agregar tipo de estadística")
        },
        text = {
            Column {
                OutlinedTextField(
                    label = { Text("Clave") },
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
                    modifier = Modifier.onFocusChanged {
                        if (!it.isFocused && keyCode.isNotEmpty()) {
                            keyCode = keyCode.padStart(3, '0')
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
                    if (statisticType != null) {
                        screenModel.editStatisticType(statisticTypeId!!, keyCode, name)
                    } else {
                        screenModel.createStatisticType(keyCode, name)
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
