package org.eduardoleolim.organizadorpec660.app.agency.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.eduardoleolim.organizadorpec660.app.agency.model.AgencyScreenModel
import org.eduardoleolim.organizadorpec660.app.agency.model.DeleteState
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.shared.composables.ErrorDialog
import org.eduardoleolim.organizadorpec660.app.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorpec660.core.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyHasInstrumentsError
import org.jetbrains.compose.resources.stringResource

@Composable
fun AgencyScreen.AgencyDeleteModal(
    screenModel: AgencyScreenModel,
    agency: AgencyResponse,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var errorOccurred by remember { mutableStateOf(false) }
    var errorText: String? by remember { mutableStateOf(null) }

    when (val deleteState = screenModel.deleteState) {
        DeleteState.Idle -> {
            errorOccurred = false
            errorText = null
        }

        DeleteState.InProgress -> {
            errorOccurred = false
            errorText = null
        }

        DeleteState.Success -> {
            onSuccess()
        }

        is DeleteState.Error -> {
            errorOccurred = true

            when (val error = deleteState.error) {
                is AgencyHasInstrumentsError -> {
                    errorText = stringResource(Res.string.ag_delete_error_has_instruments)
                }

                else -> {
                    errorText = stringResource(Res.string.ag_delete_error_default)
                    println(error)
                }
            }
        }
    }

    QuestionDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(stringResource(Res.string.ag_delete_title))
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(Res.string.ag_delete_text, agency.name))
            }
        },
        onConfirmRequest = {
            screenModel.deleteAgency(agency.id)
        },
        onDismissRequest = {
            onDismissRequest()
        }
    )

    if (errorOccurred) {
        ErrorDialog(
            text = {
                errorText?.let {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(it)
                    }
                }
            },
            onDismissRequest = {
                screenModel.resetDeleteModal()
            },
            onConfirmRequest = onDismissRequest
        )
    }
}
