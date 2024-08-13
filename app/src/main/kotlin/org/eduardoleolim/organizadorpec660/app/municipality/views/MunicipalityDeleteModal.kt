package org.eduardoleolim.organizadorpec660.app.municipality.views

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.mun_delete_text
import org.eduardoleolim.organizadorpec660.app.generated.resources.mun_delete_title
import org.eduardoleolim.organizadorpec660.app.municipality.model.MunicipalityDeleteState
import org.eduardoleolim.organizadorpec660.app.municipality.model.MunicipalityScreenModel
import org.eduardoleolim.organizadorpec660.app.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.jetbrains.compose.resources.stringResource

@Composable
fun MunicipalityScreen.MunicipalityDeleteModal(
    screenModel: MunicipalityScreenModel,
    municipality: MunicipalityResponse,
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    onDismissRequest: () -> Unit
) {
    when (val deleteState = screenModel.deleteState) {
        MunicipalityDeleteState.Idle -> {

        }

        MunicipalityDeleteState.InProgress -> {

        }

        MunicipalityDeleteState.Success -> {
            onSuccess()
        }

        is MunicipalityDeleteState.Error -> {
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
            Text(stringResource(Res.string.mun_delete_title))
        },
        text = {
            Text(stringResource(Res.string.mun_delete_text, municipality.name))
        },
        onConfirmRequest = { screenModel.deleteMunicipality(municipality.id) },
        onDismissRequest = onDismissRequest
    )
}
