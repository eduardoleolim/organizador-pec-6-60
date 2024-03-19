package org.eduardoleolim.organizadorPec660.app.municipality

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.eduardoleolim.organizadorPec660.app.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorPec660.core.municipality.application.MunicipalityResponse

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
            Text("Eliminar municipio")
        },
        text = {
            Text("¿Estás seguro que deseas eliminar el municipio ${municipality.name}?")
        },
        onConfirmRequest = {
            screenModel.deleteMunicipality(municipality.id)
        },
        onDismissRequest = onDismissRequest
    )
}
