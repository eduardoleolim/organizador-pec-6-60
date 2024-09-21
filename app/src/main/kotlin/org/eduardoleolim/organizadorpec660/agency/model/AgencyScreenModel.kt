package org.eduardoleolim.organizadorpec660.agency.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.agency.application.AgenciesResponse
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.create.CreateAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.delete.DeleteAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.application.searchById.SearchAgencyByIdQuery
import org.eduardoleolim.organizadorpec660.agency.application.searchByTerm.SearchAgenciesByTermQuery
import org.eduardoleolim.organizadorpec660.agency.application.update.UpdateAgencyCommand
import org.eduardoleolim.organizadorpec660.agency.data.EmptyAgencyDataException
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyHasInstrumentsError
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorpec660.agency.domain.CanNotDeleteAgencyError
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchById.SearchFederalEntityByIdQuery
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.resources.Res
import org.eduardoleolim.organizadorpec660.shared.resources.ag_delete_error_default
import org.eduardoleolim.organizadorpec660.shared.resources.ag_delete_error_has_instruments
import org.eduardoleolim.organizadorpec660.shared.resources.ag_delete_error_not_found
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.searchByTerm.SearchStatisticTypesByTermQuery
import org.jetbrains.compose.resources.getString

data class SearchAgencyParameters(
    val search: String?,
    val orders: List<HashMap<String, String>>?,
    val limit: Int?,
    val offset: Int?
)

class AgencyScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var agencies by mutableStateOf(AgenciesResponse(emptyList(), 0, null, null))
        private set

    var municipalities by mutableStateOf(emptyList<MunicipalityResponse>())
        private set

    var federalEntities by mutableStateOf(emptyList<FederalEntityResponse>())
        private set

    var statisticTypes by mutableStateOf(emptyList<StatisticTypeResponse>())
        private set

    var formState by mutableStateOf<AgencyFormState>(AgencyFormState.Idle)
        private set

    var deleteState by mutableStateOf<AgencyDeleteState>(AgencyDeleteState.Idle)
        private set

    var agency by mutableStateOf(AgencyFormData())
        private set

    fun searchAgency(agencyId: String?) {
        screenModelScope.launch(dispatcher) {
            agency = if (agencyId == null) {
                AgencyFormData()
            } else {
                val agencyResponse = queryBus.ask<AgencyResponse>(SearchAgencyByIdQuery(agencyId))
                val federalEntityResponse =
                    queryBus.ask<FederalEntityResponse>(SearchFederalEntityByIdQuery(agencyResponse.municipality.federalEntityId))

                AgencyFormData(
                    agencyResponse.id,
                    agencyResponse.name,
                    agencyResponse.consecutive,
                    federalEntityResponse,
                    agencyResponse.municipality,
                    agencyResponse.statisticTypes
                )
            }
        }
    }

    fun updateName(name: String) {
        agency = agency.copy(name = name)
    }

    fun updateConsecutive(consecutive: String) {
        agency = agency.copy(consecutive = consecutive)
    }

    fun updateFederalEntity(federalEntity: FederalEntityResponse) {
        if (agency.federalEntity?.id != federalEntity.id) {
            agency = agency.copy(federalEntity = federalEntity, municipality = null)
        }
    }

    fun updateMunicipality(municipality: MunicipalityResponse) {
        val simpleMunicipality = SimpleMunicipalityResponse(
            municipality.id,
            municipality.name,
            municipality.keyCode,
            municipality.federalEntity.id,
            municipality.createdAt,
            municipality.updatedAt
        )
        agency = agency.copy(municipality = simpleMunicipality)
    }

    fun addStatisticType(statisticType: StatisticTypeResponse) {
        val statisticTypes = agency.statisticTypes.toMutableList().apply {
            if (none { it.id == statisticType.id }) {
                add(statisticType)
            }
        }
        agency = agency.copy(statisticTypes = statisticTypes)
    }

    fun removeStatisticType(statisticType: StatisticTypeResponse) {
        val statisticTypes = agency.statisticTypes.toMutableList().apply { removeIf { it.id == statisticType.id } }
        agency = agency.copy(statisticTypes = statisticTypes)
    }

    fun resetForm() {
        formState = AgencyFormState.Idle
        agency = AgencyFormData()
    }

    fun resetDeleteModal() {
        deleteState = AgencyDeleteState.Idle
    }

    fun searchAgencies(
        search: String? = null,
        orders: List<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null,
    ) {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchAgenciesByTermQuery(search, orders?.toTypedArray(), limit, offset)
                agencies = queryBus.ask(query)
            } catch (e: Exception) {
                agencies = AgenciesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun searchMunicipalities(federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            municipalities = federalEntityId?.let {
                try {
                    val query = SearchMunicipalitiesByTermQuery(federalEntityId)
                    queryBus.ask<MunicipalitiesResponse>(query).municipalities
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()
        }
    }

    fun searchAllFederalEntities() {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchFederalEntitiesByTermQuery()
                federalEntities = queryBus.ask<FederalEntitiesResponse>(query).federalEntities
            } catch (e: Exception) {
                federalEntities = emptyList()
            }
        }
    }

    fun searchAllStatisticTypes() {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchStatisticTypesByTermQuery()
                statisticTypes = queryBus.ask<StatisticTypesResponse>(query).statisticTypes
            } catch (e: Exception) {
                statisticTypes = emptyList()
            }
        }
    }

    fun saveAgency() {
        if (agency.id == null) {
            createAgency()
        } else {
            updateAgency()
        }
    }

    private fun createAgency() {
        val (_, name, consecutive, _, municipality, statisticTypes) = agency
        screenModelScope.launch(dispatcher) {
            formState = AgencyFormState.InProgress
            delay(500)

            val isNameEmpty = name.isEmpty()
            val isConsecutiveEmpty = name.isEmpty()
            val isNotMunicipalitySelected = municipality == null
            val isStatisticTypesEmpty = statisticTypes.isEmpty()

            if (isNameEmpty || isConsecutiveEmpty || isNotMunicipalitySelected || isStatisticTypesEmpty) {
                formState = AgencyFormState.Error(
                    EmptyAgencyDataException(
                        isNameEmpty,
                        isConsecutiveEmpty,
                        isNotMunicipalitySelected,
                        isStatisticTypesEmpty
                    )
                )

                return@launch
            }

            try {
                val statisticTypeIds = statisticTypes.map { it.id }
                val command = CreateAgencyCommand(name, consecutive, municipality?.id!!, statisticTypeIds)
                commandBus.dispatch(command).fold(
                    ifRight = {
                        formState = AgencyFormState.SuccessCreate
                    },
                    ifLeft = {
                        formState = AgencyFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                formState = AgencyFormState.Error(e.cause!!)
            }
        }
    }

    private fun updateAgency() {
        val (id, name, consecutive, _, municipality, statisticTypes) = agency

        screenModelScope.launch(dispatcher) {
            formState = AgencyFormState.InProgress
            delay(500)

            val isNameEmpty = name.isEmpty()
            val isConsecutiveEmpty = name.isEmpty()
            val isNotMunicipalitySelected = municipality == null
            val isStatisticTypesEmpty = statisticTypes.isEmpty()

            if (isNameEmpty || isConsecutiveEmpty || isNotMunicipalitySelected || isStatisticTypesEmpty) {
                formState = AgencyFormState.Error(
                    EmptyAgencyDataException(
                        isNameEmpty,
                        isConsecutiveEmpty,
                        isNotMunicipalitySelected,
                        isStatisticTypesEmpty
                    )
                )

                return@launch
            }

            try {
                val statisticTypeIds = statisticTypes.map { it.id }
                val command = UpdateAgencyCommand(id!!, name, consecutive, municipality?.id!!, statisticTypeIds)
                commandBus.dispatch(command).fold(
                    ifRight = {
                        formState = AgencyFormState.SuccessEdit
                    },
                    ifLeft = {
                        formState = AgencyFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                formState = AgencyFormState.Error(e.cause!!)
            }
        }
    }

    fun deleteAgency(agencyId: String) {
        screenModelScope.launch(dispatcher) {
            deleteState = AgencyDeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteAgencyCommand(agencyId)).foldAsync(
                    ifRight = {
                        deleteState = AgencyDeleteState.Success
                    },
                    ifLeft = { error ->
                        val message = when (error) {
                            is AgencyNotFoundError -> getString(Res.string.ag_delete_error_not_found)

                            is AgencyHasInstrumentsError -> getString(Res.string.ag_delete_error_has_instruments)

                            is CanNotDeleteAgencyError -> getString(Res.string.ag_delete_error_default)

                            else -> getString(Res.string.ag_delete_error_default)
                        }

                        deleteState = AgencyDeleteState.Error(message)
                    }
                )
            } catch (e: Exception) {
                deleteState = AgencyDeleteState.Error(getString(Res.string.ag_delete_error_default))
            }
        }
    }
}
