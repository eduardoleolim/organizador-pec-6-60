package org.eduardoleolim.organizadorPec660.app.federalEntity

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.eduardoleolim.organizadorPec660.app.shared.components.dialogs.QuestionDialog
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.FederalEntityResponse

@Composable
fun FederalEntityScreen.FederalEntityDeleteModal(
    screenModel: FederalEntityScreenModel,
    selectedFederalEntity: FederalEntityResponse,
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    onDismissRequest: () -> Unit
) {
    QuestionDialog(
        title = {
            Text("Eliminar entidad federativa")
        },
        text = {
            Text("¿Estás seguro de que deseas eliminar la entidad federativa ${selectedFederalEntity.name}?")
        },
        onConfirmRequest = {
            screenModel.deleteFederalEntity(selectedFederalEntity.id) { result ->
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
        onDismissRequest = {
            onDismissRequest()
        }
    )
}
