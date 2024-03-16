package org.eduardoleolim.organizadorPec660.app.instrumentType

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.eduardoleolim.organizadorPec660.app.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorPec660.core.instrumentType.application.InstrumentTypeResponse

@Composable
fun InstrumentTypeScreen.InstrumentTypeDeleteModal(
    screenModel: InstrumentTypeScreenModel,
    selectedInstrumentType: InstrumentTypeResponse,
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
            Text("¿Estás seguro de que deseas eliminar la entidad federativa ${selectedInstrumentType.name}?")
        },
        onConfirmRequest = {
            screenModel.deleteInstrumentType(selectedInstrumentType.id)
        },
        onDismissRequest = {
            onDismissRequest()
        }
    )
}
