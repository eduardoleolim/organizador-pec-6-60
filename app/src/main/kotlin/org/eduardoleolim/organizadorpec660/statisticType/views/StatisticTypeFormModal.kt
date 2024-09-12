package org.eduardoleolim.organizadorpec660.statisticType.views

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
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.data.EmptyStatisticTypeDataException
import org.eduardoleolim.organizadorpec660.statisticType.domain.InvalidStatisticTypeKeyCodeError
import org.eduardoleolim.organizadorpec660.statisticType.domain.InvalidStatisticTypeNameError
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeAlreadyExistsError
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeFormState
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeScreenModel
import org.jetbrains.compose.resources.stringResource

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

    val titleResource = remember {
        if (statisticType == null) Res.string.st_form_add_title else Res.string.st_form_edit_title
    }
    var enabled by remember { mutableStateOf(true) }
    var isKeyCodeError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var keyCodeSupportingText: String? by remember { mutableStateOf(null) }
    var nameSupportingText: String? by remember { mutableStateOf(null) }

    when (val formState = screenModel.formState) {
        StatisticTypeFormState.Idle -> {
            enabled = true
            isKeyCodeError = false
            isNameError = false
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        StatisticTypeFormState.InProgress -> {
            enabled = false
            isKeyCodeError = false
            isNameError = false
            keyCodeSupportingText = null
            nameSupportingText = null
        }

        StatisticTypeFormState.SuccessCreate -> {
            enabled = true
            onSuccess()
        }

        StatisticTypeFormState.SuccessEdit -> {
            enabled = false
            onSuccess()
        }

        is StatisticTypeFormState.Error -> {
            enabled = true

            when (val error = formState.error) {
                is StatisticTypeAlreadyExistsError -> {
                    keyCodeSupportingText = stringResource(Res.string.st_form_already_exists)
                    isKeyCodeError = true
                }

                is InvalidStatisticTypeKeyCodeError -> {
                    keyCodeSupportingText = stringResource(Res.string.st_form_keycode_format)
                    isKeyCodeError = true
                }

                is InvalidStatisticTypeNameError -> {
                    nameSupportingText = stringResource(Res.string.st_form_name_required)
                    isNameError = true
                }

                is EmptyStatisticTypeDataException -> {
                    if (error.isKeyCodeEmpty) {
                        keyCodeSupportingText = stringResource(Res.string.st_form_keycode_required)
                        isKeyCodeError = true
                    }

                    if (error.isNameEmpty) {
                        nameSupportingText = stringResource(Res.string.st_form_name_required)
                        isNameError = true
                    }
                }

                else -> {
                    println(error)
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
                OutlinedTextField(
                    label = {
                        Text(stringResource(Res.string.st_keycode))
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

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    label = {
                        Text(stringResource(Res.string.st_name))
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
                    if (statisticType != null) {
                        screenModel.editStatisticType(statisticTypeId!!, keyCode, name)
                    } else {
                        screenModel.createStatisticType(keyCode, name)
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
