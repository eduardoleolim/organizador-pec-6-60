package org.eduardoleolim.organizadorpec660.app.municipality

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.eduardoleolim.organizadorpec660.app.shared.components.dialogs.QuestionDialog
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse

@Composable
fun MunicipalityScreen.MunicipalityDeleteModal(
    screenModel: MunicipalityScreenModel,
    selectedMunicipality: MunicipalityResponse,
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    onDismissRequest: () -> Unit
) {
    QuestionDialog(
        title = {
            Text("Eliminar municipio")
        },
        text = {
            Text("¿Estás seguro que deseas eliminar el municipio ${selectedMunicipality.name}?")
        },
        onConfirmRequest = {
            screenModel.deleteMunicipality(selectedMunicipality.id) { result ->
                result.fold(
                    onSuccess = {
                        onSuccess()
                    },
                    onFailure = {
                        println(it.localizedMessage)
                        onFail()
                    }
                )
            }
        },
        onDismissRequest = onDismissRequest
    )
}
