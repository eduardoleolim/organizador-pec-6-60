package org.eduardoleolim.organizadorpec660.statisticType.views

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.st_delete_text
import org.eduardoleolim.organizadorpec660.app.generated.resources.st_delete_title
import org.eduardoleolim.organizadorpec660.shared.composables.QuestionDialog
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeDeleteState
import org.eduardoleolim.organizadorpec660.statisticType.model.StatisticTypeScreenModel
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatisticTypeScreen.StatisticTypeDeleteModal(
    screenModel: StatisticTypeScreenModel,
    statisticType: StatisticTypeResponse,
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    onDismissRequest: () -> Unit
) {
    when (val deleteState = screenModel.deleteState) {
        StatisticTypeDeleteState.Idle -> {

        }

        StatisticTypeDeleteState.InProgress -> {

        }

        StatisticTypeDeleteState.Success -> {
            onSuccess()
        }

        is StatisticTypeDeleteState.Error -> {
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
            Text(stringResource(Res.string.st_delete_title))
        },
        text = {
            Text(stringResource(Res.string.st_delete_text, statisticType.name))
        },
        onConfirmRequest = {
            screenModel.deleteStatisticType(statisticType.id)
        },
        onDismissRequest = {
            onDismissRequest()
        }
    )
}
