package org.eduardoleolim.organizadorpec660.app.municipality.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.mun_delete_text
import org.eduardoleolim.organizadorpec660.app.generated.resources.mun_delete_title
import org.eduardoleolim.organizadorpec660.app.municipality.model.DeleteState
import org.eduardoleolim.organizadorpec660.app.municipality.model.MunicipalityScreenModel
import org.eduardoleolim.organizadorpec660.app.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MunicipalityScreen.MunicipalityDeleteModal(
    screenModel: MunicipalityScreenModel,
    municipality: MunicipalityResponse,
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    onDismissRequest: () -> Unit
) {
    when (val deleteState = screenModel.deleteState.value) {
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
        title = {
            Text(stringResource(Res.string.mun_delete_title))
        },
        text = {
            Text(stringResource(Res.string.mun_delete_text, municipality.name))
        },
        onConfirmRequest = {
            screenModel.deleteMunicipality(municipality.id)
        },
        onDismissRequest = onDismissRequest
    )
}
