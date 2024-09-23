package org.eduardoleolim.organizadorpec660.federalEntity.views

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
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.model.FederalEntityDeleteState
import org.eduardoleolim.organizadorpec660.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorpec660.shared.composables.ErrorDialog
import org.eduardoleolim.organizadorpec660.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.fe_delete_text
import org.eduardoleolim.organizadorpec660.shared.resources.fe_delete_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun FederalEntityScreen.FederalEntityDeleteModal(
    screenModel: FederalEntityScreenModel,
    federalEntity: FederalEntityResponse,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var errorOccurred by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            screenModel.resetDeleteModal()
        }
    }

    when (val deleteState = screenModel.deleteState) {
        FederalEntityDeleteState.Idle -> {
            errorOccurred = false
            errorText = null
        }

        FederalEntityDeleteState.InProgress -> {
            errorOccurred = false
            errorText = null
        }

        FederalEntityDeleteState.Success -> {
            onSuccess()
        }

        is FederalEntityDeleteState.Error -> {
            errorOccurred = true
            errorText = deleteState.message
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
            Text(stringResource(Res.string.fe_delete_title))
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(Res.string.fe_delete_text, federalEntity.name))
            }
        },
        onConfirmRequest = { screenModel.deleteFederalEntity(federalEntity.id) },
        onDismissRequest = { onDismissRequest() }
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
            onDismissRequest = { screenModel.resetDeleteModal() },
            onConfirmRequest = onDismissRequest
        )
    }
}
