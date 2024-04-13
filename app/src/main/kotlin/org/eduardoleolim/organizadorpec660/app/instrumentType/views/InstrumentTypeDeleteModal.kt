package org.eduardoleolim.organizadorpec660.app.instrumentType.views

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.it_delete_text
import org.eduardoleolim.organizadorpec660.app.generated.resources.it_delete_title
import org.eduardoleolim.organizadorpec660.app.instrumentType.model.DeleteState
import org.eduardoleolim.organizadorpec660.app.instrumentType.model.InstrumentTypeScreenModel
import org.eduardoleolim.organizadorpec660.app.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.InstrumentTypeResponse
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun InstrumentTypeScreen.InstrumentTypeDeleteModal(
    screenModel: InstrumentTypeScreenModel,
    instrumentType: InstrumentTypeResponse,
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    onDismissRequest: () -> Unit
) {
    when (val deleteState = screenModel.deleteState) {
        DeleteState.Idle -> {

        }

        DeleteState.InProgress -> {

        }

        DeleteState.Success -> {
            onSuccess()
        }

        is DeleteState.Error -> {
            onFail()
            println(deleteState.error.message)
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
            Text(stringResource(Res.string.it_delete_title))
        },
        text = {
            Text(stringResource(Res.string.it_delete_text, instrumentType.name))
        },
        onConfirmRequest = {
            screenModel.deleteInstrumentType(instrumentType.id)
        },
        onDismissRequest = {
            onDismissRequest()
        }
    )
}
