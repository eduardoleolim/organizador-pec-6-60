package org.eduardoleolim.organizadorpec660.app.federalEntity.views

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
import org.eduardoleolim.organizadorpec660.app.federalEntity.model.DeleteState
import org.eduardoleolim.organizadorpec660.app.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.shared.composables.ErrorDialog
import org.eduardoleolim.organizadorpec660.app.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityHasMunicipalitiesError
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FederalEntityScreen.FederalEntityDeleteModal(
    screenModel: FederalEntityScreenModel,
    federalEntity: FederalEntityResponse,
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
                is FederalEntityHasMunicipalitiesError -> {
                    errorText = stringResource(Res.string.fe_delete_error_has_municipalities)
                }

                else -> {
                    errorText = stringResource(Res.string.fe_delete_error_default)
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
            Text(stringResource(Res.string.fe_delete_title))
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(Res.string.fe_delete_text, federalEntity.name))
            }
        },
        onConfirmRequest = {
            screenModel.deleteFederalEntity(federalEntity.id)
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
