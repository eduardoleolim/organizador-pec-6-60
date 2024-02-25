package org.eduardoleolim.organizadorPec660.app.federalEntity

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.eduardoleolim.organizadorPec660.app.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.FederalEntityResponse

@Composable
fun FederalEntityScreen.FederalEntityDeleteModal(
    screenModel: FederalEntityScreenModel,
    selectedFederalEntity: FederalEntityResponse,
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
            Text("Eliminar entidad federativa")
        },
        text = {
            Text("¿Estás seguro de que deseas eliminar la entidad federativa ${selectedFederalEntity.name}?")
        },
        onConfirmRequest = {
            screenModel.deleteFederalEntity(selectedFederalEntity.id)
        },
        onDismissRequest = {
            onDismissRequest()
        }
    )
}
