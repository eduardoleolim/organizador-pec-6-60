package org.eduardoleolim.organizadorPec660.app.federalEntity.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.eduardoleolim.organizadorPec660.app.federalEntity.model.DeleteState
import org.eduardoleolim.organizadorPec660.app.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorPec660.app.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.FederalEntityResponse

@Composable
fun FederalEntityScreen.FederalEntityDeleteModal(
    screenModel: FederalEntityScreenModel,
    federalEntity: FederalEntityResponse,
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
            Text("¿Estás seguro de que deseas eliminar la entidad federativa ${federalEntity.name}?")
        },
        onConfirmRequest = {
            screenModel.deleteFederalEntity(federalEntity.id)
        },
        onDismissRequest = {
            onDismissRequest()
        }
    )
}
