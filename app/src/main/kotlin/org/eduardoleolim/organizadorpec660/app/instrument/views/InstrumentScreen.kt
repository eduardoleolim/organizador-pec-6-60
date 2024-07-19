package org.eduardoleolim.organizadorpec660.app.instrument.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.instruments
import org.eduardoleolim.organizadorpec660.app.instrument.model.InstrumentScreenModel
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.jetbrains.compose.resources.stringResource

class InstrumentScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { InstrumentScreenModel(queryBus, commandBus) }

        Column(modifier = Modifier.padding(24.dp)) {
            InstrumentScreenHeader(
                onSaveRequest = {},
                onImportExportRequest = {}
            )

        }
    }

    @Composable
    fun InstrumentScreenHeader(
        onSaveRequest: () -> Unit,
        onImportExportRequest: () -> Unit
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.instruments),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.weight(1.0f)
            )

            SmallFloatingActionButton(
                onClick = onImportExportRequest,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.ImportExport,
                    contentDescription = "Import/Export instruments"
                )
            }

            SmallFloatingActionButton(
                onClick = onSaveRequest,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add instrument"
                )
            }
        }
    }
}
