package org.eduardoleolim.organizadorpec660.app.municipality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse

@Composable
fun MunicipalityScreen.MunicipalityFormModal(
    screenModel: MunicipalityScreenModel,
    selectedMunicipality: MunicipalityResponse?,
    onDismissRequest: () -> Unit,
    onSuccess: () -> Unit
) {
    val municipalityId by remember { mutableStateOf(selectedMunicipality?.id) }
    var federalEntityId by remember { mutableStateOf(selectedMunicipality?.federalEntity?.id) }
    var KeyCode by remember { mutableStateOf(selectedMunicipality?.keyCode ?: "") }
    var name by remember { mutableStateOf(selectedMunicipality?.name ?: "") }
    var isFederalEntityError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(selectedMunicipality?.let { "Editar municipio" } ?: "Agregar municipio")
        },
        text = {
            Column {
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    var federalEntities by remember { mutableStateOf<List<FederalEntityResponse>>(emptyList()) }
                    var selectedFederalEntity by remember { mutableStateOf<FederalEntityResponse?>(null) }

                    LaunchedEffect(selectedFederalEntity) {
                        federalEntityId = selectedFederalEntity?.id
                    }

                    screenModel.allFederalEntities { result ->
                        result.fold(
                            onSuccess = {
                                federalEntities = it
                                if (federalEntityId != null) {
                                    selectedFederalEntity =
                                        it.find { federalEntity -> federalEntity.id == federalEntityId }
                                }
                            },
                            onFailure = {
                                println(it.localizedMessage)
                                federalEntities = emptyList()
                            }
                        )
                    }

                    OutlinedTextField(
                        value = selectedFederalEntity?.let { "${it.keyCode} - ${it.name}" }
                            ?: "Selecciona una entidad federativa",
                        onValueChange = {},
                        readOnly = true,
                        supportingText = {
                            if (isFederalEntityError) {
                                Text("Selecciona una entidad federativa", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { expanded = true },
                                content = {
                                    Icon(
                                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "Expand"
                                    )
                                }
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(text = "Todas las entidades", color = MaterialTheme.colorScheme.onSurface)
                            },
                            onClick = {
                                expanded = false
                                selectedFederalEntity = null
                            }
                        )

                        federalEntities.forEach { federalEntity ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${federalEntity.keyCode} - ${federalEntity.name}",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    selectedFederalEntity = federalEntity
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = KeyCode,
                    onValueChange = { KeyCode = it },
                    label = { Text("Clave") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (federalEntityId == null) {
                        isFederalEntityError = true
                        return@TextButton
                    }

                    if (municipalityId == null) {
                        screenModel.createMunicipality(
                            keyCode = KeyCode,
                            name = name,
                            federalEntityId = federalEntityId!!
                        ) {
                            it.fold(
                                onSuccess = {
                                    onSuccess()
                                },
                                onFailure = {
                                    println(it.localizedMessage)
                                }
                            )
                        }
                    } else {
                        screenModel.editMunicipality(
                            municipalityId = municipalityId!!,
                            keyCode = KeyCode,
                            name = name,
                            federalEntityId = federalEntityId!!
                        ) {
                            it.fold(
                                onSuccess = {
                                    onSuccess()
                                },
                                onFailure = {
                                    println(it.localizedMessage)
                                }
                            )
                        }
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancelar")
            }
        }
    )
}
