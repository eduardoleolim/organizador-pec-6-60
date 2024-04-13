package org.eduardoleolim.organizadorpec660.app.instrumentType.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogProperties
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.instrumentType.data.EmptyInstrumentTypeDataException
import org.eduardoleolim.organizadorpec660.app.instrumentType.model.FormState
import org.eduardoleolim.organizadorpec660.app.instrumentType.model.InstrumentTypeScreenModel
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.InstrumentTypeResponse
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InvalidInstrumentTypeNameError
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
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

    when (val formState = screenModel.formState) {
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
                    nameSupportingText = stringResource(Res.string.it_form_name_format)
                    isNameError = true
                }

                is InstrumentTypeAlreadyExistsError -> {
                    nameSupportingText = stringResource(Res.string.it_form_already_exists)
                    isNameError = true
                }

                is EmptyInstrumentTypeDataException -> {
                    nameSupportingText = stringResource(Res.string.it_form_name_required)
                    isNameError = true
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
            val textTitle = if (instrumentType == null) {
                stringResource(Res.string.it_form_add_title)
            } else {
                stringResource(Res.string.it_form_edit_title)
            }

            Text(textTitle)
        },
        text = {
            Column {
                OutlinedTextField(
                    label = { Text(stringResource(Res.string.it_name)) },
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
