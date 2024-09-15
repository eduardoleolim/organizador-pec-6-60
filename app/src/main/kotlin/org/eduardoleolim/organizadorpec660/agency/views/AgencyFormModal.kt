package org.eduardoleolim.organizadorpec660.agency.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.material3.DataTable
import kotlinx.coroutines.delay
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.data.EmptyAgencyDataException
import org.eduardoleolim.organizadorpec660.agency.model.AgencyFormState
import org.eduardoleolim.organizadorpec660.agency.model.AgencyScreenModel
import org.eduardoleolim.organizadorpec660.shared.composables.OutlinedSelect
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.jetbrains.compose.resources.stringResource

@Composable
fun AgencyScreen.AgencyFormModal(
    screenModel: AgencyScreenModel,
    agency: AgencyResponse?,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val agencyId = remember { agency?.id }
    var name by remember { mutableStateOf(agency?.name ?: "") }
    var consecutive by remember { mutableStateOf(agency?.consecutive ?: "") }
    var federalEntityIndex by remember { mutableStateOf<Int?>(null) }
    var federalEntityId by remember { mutableStateOf(agency?.municipality?.federalEntityId) }
    var municipalityIndex by remember { mutableStateOf<Int?>(null) }
    var municipalityId by remember { mutableStateOf(agency?.municipality?.id) }
    var statisticType by remember { mutableStateOf<StatisticTypeResponse?>(null) }
    var statisticTypes by remember { mutableStateOf(agency?.statisticTypes ?: emptyList()) }

    val titleResource = remember { if (agency == null) Res.string.ag_form_add_title else Res.string.ag_form_edit_title }
    var enabled by remember { mutableStateOf(true) }
    var isNameError by remember { mutableStateOf(false) }
    var isConsecutiveError by remember { mutableStateOf(false) }
    var isFederalEntityError by remember { mutableStateOf(false) }
    var isMunicipalityError by remember { mutableStateOf(false) }
    var isStatisticTypeError by remember { mutableStateOf(false) }
    var nameSupportingText by mutableStateOf<String?>(null)
    var consecutiveSupportingText by mutableStateOf<String?>(null)
    var federalEntitySupportingText by remember { mutableStateOf<String?>(null) }
    var municipalitySupportingText by remember { mutableStateOf<String?>(null) }
    var statisticTypeSupportingText by remember { mutableStateOf<String?>(null) }

    val formScrollState = rememberScrollState()

    val statisticTypesColumns = remember {
        listOf(
            DataColumn(
                header = {
                    Text(stringResource(Res.string.ag_form_statistic_type))
                }
            ),
            DataColumn(
                width = TableColumnWidth.Fraction(0.3f),
                alignment = Alignment.Center,
                header = { }
            )
        )
    }

    LaunchedEffect(Unit) {
        val agencyFederalEntityId = agency?.municipality?.federalEntityId
        val agencyMunicipalityId = agency?.municipality?.id

        screenModel.searchAllFederalEntities()
        screenModel.searchAllStatisticTypes()

        delay(200)

        agencyFederalEntityId?.let { id ->
            federalEntityIndex = screenModel.federalEntities.indexOfFirst { it.id == id }

            screenModel.searchMunicipalities(id)
            delay(200)
        }

        agencyMunicipalityId?.let { id ->
            municipalityIndex = screenModel.municipalities.indexOfFirst { it.id == id }
        }
    }

    LaunchedEffect(federalEntityId) {
        screenModel.searchMunicipalities(federalEntityId)
        municipalityIndex = null
        municipalityId = null
    }

    when (val formState = screenModel.formState) {
        AgencyFormState.Idle -> {
            enabled = true
            isNameError = false
            isConsecutiveError = false
            isFederalEntityError = false
            isMunicipalityError = false
            isStatisticTypeError = false
            nameSupportingText = null
            consecutiveSupportingText = null
            federalEntitySupportingText = null
            municipalitySupportingText = null
            statisticTypeSupportingText = null
        }

        AgencyFormState.InProgress -> {
            enabled = false
            isNameError = false
            isConsecutiveError = false
            isFederalEntityError = false
            isMunicipalityError = false
            isStatisticTypeError = false
            nameSupportingText = null
            consecutiveSupportingText = null
            federalEntitySupportingText = null
            municipalitySupportingText = null
            statisticTypeSupportingText = null
        }

        AgencyFormState.SuccessCreate -> {
            enabled = true
            onSuccess()
        }

        AgencyFormState.SuccessEdit -> {
            enabled = true
            onSuccess()
        }

        is AgencyFormState.Error -> {
            enabled = true

            when (val error = formState.error) {

                is EmptyAgencyDataException -> {
                    if (error.isNameEmpty) {
                        nameSupportingText = stringResource(Res.string.ag_form_name_required)
                        isNameError = true
                    }

                    if (error.isConsecutiveEmpty) {
                        consecutiveSupportingText = stringResource(Res.string.ag_form_consecutive_required)
                        isConsecutiveError = true
                    }

                    if (error.isMunicipalityEmpty) {
                        municipalitySupportingText = stringResource(Res.string.ag_form_municipality_required)
                        isMunicipalityError = true
                    }

                    if (error.isStatisticTypesEmpty) {
                        statisticTypeSupportingText = stringResource(Res.string.ag_form_statistic_type_required)
                        isStatisticTypeError = true
                    }
                }

                else -> {
                    println("Error: ${error.message}")
                }
            }
        }
    }

    AlertDialog(
        modifier = Modifier.heightIn(max = 500.dp),
        properties = DialogProperties(
            dismissOnClickOutside = false
        ),
        onDismissRequest = onDismissRequest,
        title = {
            Text(stringResource(titleResource))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(formScrollState)
            ) {
                Text(
                    text = stringResource(Res.string.ag_form_general),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    OutlinedTextField(
                        label = {
                            Text(stringResource(Res.string.ag_name))
                        },
                        value = name,
                        onValueChange = { name = it.uppercase() },
                        singleLine = true,
                        isError = isNameError,
                        supportingText = nameSupportingText?.let { message ->
                            { Text(text = message, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.width(240.dp)
                    )

                    Spacer(Modifier.width(32.dp))

                    OutlinedTextField(
                        label = {
                            Text(stringResource(Res.string.ag_consecutive))
                        },
                        value = consecutive,
                        onValueChange = {
                            if (it.isBlank()) {
                                consecutive = ""
                            } else if (Regex("^\\d{0,4}$").matches(it)) {
                                consecutive = it
                            }
                        },
                        singleLine = true,
                        isError = isConsecutiveError,
                        supportingText = consecutiveSupportingText?.let { message ->
                            { Text(text = message, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.width(240.dp)
                            .onFocusChanged {
                                if (!it.isFocused && consecutive.isNotEmpty()) {
                                    consecutive = consecutive.padStart(4, '0')
                                }
                            }
                    )
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    text = stringResource(Res.string.ag_form_municipality),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    OutlinedSelect(
                        items = screenModel.federalEntities,
                        index = federalEntityIndex,
                        onValueSelected = { index, item ->
                            federalEntityIndex = index
                            federalEntityId = item.id
                        },
                        visualTransformation = { "${it.keyCode} - ${it.name}" },
                        label = {
                            Text(stringResource(Res.string.mun_federal_entity))
                        },
                        isError = isFederalEntityError,
                        supportingText = federalEntitySupportingText?.let { message ->
                            { Text(text = message, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.width(240.dp)
                    )

                    Spacer(Modifier.width(32.dp))

                    OutlinedSelect(
                        enabled = federalEntityId != null,
                        items = screenModel.municipalities,
                        index = municipalityIndex,
                        onValueSelected = { index, item ->
                            municipalityIndex = index
                            municipalityId = item.id
                        },
                        visualTransformation = { "${it.keyCode} - ${it.name}" },
                        label = {
                            Text(stringResource(Res.string.ag_form_municipality))
                        },
                        isError = isMunicipalityError,
                        supportingText = municipalitySupportingText?.let { message ->
                            { Text(text = message, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.width(240.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    text = stringResource(Res.string.ag_form_statistic_types),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Column {
                    Row {
                        OutlinedSelect(
                            items = screenModel.statisticTypes,
                            onValueSelected = { _, item -> statisticType = item },
                            visualTransformation = { "${it.keyCode} - ${it.name}" },
                            label = {
                                Text(stringResource(Res.string.ag_form_statistic_type))
                            },
                            isError = isStatisticTypeError,
                            supportingText = statisticTypeSupportingText?.let { message ->
                                { Text(text = message, color = MaterialTheme.colorScheme.error) }
                            },
                            modifier = Modifier.width(240.dp)
                        )

                        Spacer(Modifier.width(32.dp))

                        IconButton(
                            enabled = statisticType != null,
                            onClick = {
                                statisticType?.let {
                                    if (statisticTypes.contains(statisticType).not()) {
                                        statisticTypes = statisticTypes.toMutableList().apply { add(it) }
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add statistic type"
                            )
                        }
                    }

                    DataTable(
                        modifier = Modifier.fillMaxWidth(),
                        columns = statisticTypesColumns
                    ) {
                        statisticTypes.forEach { item ->
                            row {
                                cell {
                                    Text("${item.keyCode} - ${item.name}")
                                }

                                cell {
                                    IconButton(
                                        onClick = {
                                            statisticTypes = statisticTypes.toMutableList().apply { remove(item) }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove statistic type"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = enabled,
                onClick = {
                    if (agencyId == null) {
                        screenModel.createAgency(name, consecutive, municipalityId, statisticTypes.map { it.id })
                    } else {
                        screenModel.updateAgency(
                            agencyId,
                            name,
                            consecutive,
                            municipalityId,
                            statisticTypes.map { it.id }
                        )
                    }
                }
            ) {
                Text(stringResource(Res.string.save))
            }
        },
        dismissButton = {
            TextButton(
                enabled = enabled,
                onClick = onDismissRequest,
            ) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}
