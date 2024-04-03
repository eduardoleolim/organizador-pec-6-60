package org.eduardoleolim.organizadorPec660.core.agency.domain

import java.util.*

class Agency private constructor(
    private val id: AgencyId,
    private var name: AgencyName,
    private var consecutive: AgencyConsecutive,
    private val municipalities: MutableList<AgencyMunicipalityAssociation>,
    private val statisticTypes: MutableList<AgencyStatisticTypeAssociation>
) {
    companion object {
        fun create(
            name: String,
            consecutive: Int,
            municipalities: List<Pair<String, Boolean>>,
            statisticTypes: List<Pair<String, String>>
        ): Agency {
            val agencyId = AgencyId.random()
            val agencyName = AgencyName(name)
            val agencyConsecutive = AgencyConsecutive(consecutive)

            if (municipalities.isEmpty()) {
                throw InvalidAgencyMunicipalitiesError()
            }

            if (statisticTypes.isEmpty()) {
                throw InvalidAgencyStatisticTypesError()
            }

            val municipalityAssociations = municipalities.map { association ->
                val (municipalityId, isOwner) = association

                AgencyMunicipalityAssociation.from(agencyId.value.toString(), municipalityId, isOwner)
            }.distinctBy { it.municipalityId() }

            val statisticTypeAssociations = statisticTypes.map { association ->
                val (statisticTypeId, instrumentTypeId) = association

                AgencyStatisticTypeAssociation.from(agencyId.value.toString(), statisticTypeId, instrumentTypeId)
            }.distinctBy { it.statisticTypeId() }

            return Agency(
                agencyId,
                agencyName,
                agencyConsecutive,
                municipalityAssociations.toMutableList(),
                statisticTypeAssociations.toMutableList()
            )
        }

        fun from(
            id: String,
            name: String,
            consecutive: Int,
            municipalities: List<Pair<String, Boolean>>,
            statisticTypes: List<Pair<String, String>>
        ): Agency {
            val agencyId = AgencyId.fromString(id)
            val agencyName = AgencyName(name)
            val agencyConsecutive = AgencyConsecutive(consecutive)

            if (municipalities.isEmpty()) {
                throw InvalidAgencyMunicipalitiesError()
            }

            if (statisticTypes.isEmpty()) {
                throw InvalidAgencyStatisticTypesError()
            }

            val municipalityAssociations = municipalities.map { association ->
                val (municipalityId, isOwner) = association

                AgencyMunicipalityAssociation.from(agencyId.value.toString(), municipalityId, isOwner)
            }.distinctBy { it.municipalityId() }

            val statisticTypeAssociations = statisticTypes.map { association ->
                val (statisticTypeId, instrumentTypeId) = association

                AgencyStatisticTypeAssociation.from(agencyId.value.toString(), statisticTypeId, instrumentTypeId)
            }.distinctBy { it.statisticTypeId() }

            return Agency(
                agencyId,
                agencyName,
                agencyConsecutive,
                municipalityAssociations.toMutableList(),
                statisticTypeAssociations.toMutableList()
            )
        }
    }

    fun id() = id.value

    fun name() = name.value

    fun consecutive() = consecutive.value

    fun municipalities() = municipalities.toList()

    fun statisticTypes() = statisticTypes.toList()

    fun changeName(name: String) {
        this.name = AgencyName(name)
    }

    fun changeConsecutive(consecutive: Int) {
        this.consecutive = AgencyConsecutive(consecutive)
    }

    fun addMunicipality(municipalityId: String, isOwner: Boolean) {
        val municipality = municipalities.firstOrNull { it.municipalityId().toString() == municipalityId }

        if (municipality != null) {
            municipality.changeOwner(isOwner)
        } else {
            municipalities.add(
                AgencyMunicipalityAssociation.from(
                    id.value.toString(),
                    municipalityId,
                    isOwner
                )
            )
        }
    }

    fun removeMunicipality(municipalityId: String) {
        municipalities.removeAll { it.municipalityId().toString() == municipalityId }
    }

    fun changeMunicipalityOwner(municipalityId: String, isOwner: Boolean) {
        municipalities.firstOrNull { it.municipalityId().toString() == municipalityId }?.changeOwner(isOwner)
    }

    fun addStatisticType(statisticTypeId: String, instrumentTypeId: String) {
        val statisticType = statisticTypes.firstOrNull { it.statisticTypeId().toString() == statisticTypeId }

        if (statisticType != null) {
            statisticType.changeInstrumentTypeId(instrumentTypeId)
        } else {
            statisticTypes.add(
                AgencyStatisticTypeAssociation.from(
                    id.value.toString(),
                    statisticTypeId,
                    instrumentTypeId
                )
            )
        }
    }

    fun removeStatisticType(statisticType: String) {
        statisticTypes.removeAll { it.statisticTypeId().toString() == statisticType }
    }

    fun changeStatisticTypeInstrumentType(statisticTypeId: String, instrumentTypeId: String) {
        statisticTypes.firstOrNull { it.statisticTypeId().toString() == statisticTypeId }
            ?.changeInstrumentTypeId(instrumentTypeId)
    }
}

data class AgencyId(val value: UUID) {
    companion object {
        fun random() = AgencyId(UUID.randomUUID())

        fun fromString(value: String) = try {
            AgencyId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidAgencyIdError(value, e)
        }
    }
}

data class AgencyName(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (value.isBlank()) {
            throw InvalidAgencyNameError(value)
        }
    }
}

data class AgencyConsecutive(val value: Int) {
    init {
        validate()
    }

    private fun validate() {
        if (value <= 0) {
            throw InvalidAgencyConsecutiveError(value)
        }
    }
}
