package org.eduardoleolim.organizadorpec660.agency.views

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
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.model.AgencyDeleteState
import org.eduardoleolim.organizadorpec660.agency.model.AgencyScreenModel
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.ag_delete_text
import org.eduardoleolim.organizadorpec660.app.generated.resources.ag_delete_title
import org.eduardoleolim.organizadorpec660.shared.composables.ErrorDialog
import org.eduardoleolim.organizadorpec660.shared.composables.QuestionDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun AgencyScreen.AgencyDeleteModal(
    screenModel: AgencyScreenModel,
    agency: AgencyResponse,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var errorOccurred by remember { mutableStateOf(false) }
    var errorMessage: String? by remember { mutableStateOf(null) }

    when (val deleteState = screenModel.deleteState) {
        AgencyDeleteState.Idle -> {
            errorOccurred = false
            errorMessage = null
        }

        AgencyDeleteState.InProgress -> {
            errorOccurred = false
            errorMessage = null
        }

        AgencyDeleteState.Success -> {
            onSuccess()
        }

        is AgencyDeleteState.Error -> {
            errorOccurred = true
            errorMessage = deleteState.message
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
        onConfirmRequest = { screenModel.deleteAgency(agency.id) },
        onDismissRequest = { onDismissRequest() }
    )

    if (errorOccurred) {
        ErrorDialog(
            text = {
                errorMessage?.let {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(it)
                    }
                }
            },
            onDismissRequest = { screenModel.resetDeleteModal() },
            onConfirmRequest = onDismissRequest
        )
    }
}
