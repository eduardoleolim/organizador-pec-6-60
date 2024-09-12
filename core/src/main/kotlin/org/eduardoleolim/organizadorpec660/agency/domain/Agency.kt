package org.eduardoleolim.organizadorpec660.agency.domain

import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityId
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeId
import java.util.*

/**
 * The agencies provide monthly summary reports of statistics for each municipality to INEGI
 *
 * @property id The id of the agency
 * @property name The name of the agency
 * @property consecutive The consecutive key of the agency
 * @property municipalityId The id of municipality where the agency is located
 * @property statisticTypeIds The ids of statistic types used to generate the summary reports
 * @property createdAt The date when the agency was registered in the system
 * @property updatedAt The date of last update of agency
 * @see org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
 * @see org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
 * @constructor Create Agency
 */
class Agency private constructor(
    private val id: AgencyId,
    private var name: AgencyName,
    private var consecutive: AgencyConsecutive,
    private var municipalityId: MunicipalityId,
    private val statisticTypeIds: MutableList<StatisticTypeId>,
    private val createdAt: AgencyCreateDate,
    private var updatedAt: AgencyUpdateDate?
) {
    companion object {
        /**
         * Create an entity of [Agency] from primitive values with random id
         *
         * @param name The name of agency
         * @param consecutive The consecutive key of agency
         * @param municipalityId The id of municipality where the agency is located
         * @param statisticTypeIds The ids of statistic types used to generate the summary reports
         * @return An entity of Agency
         */
        fun create(
            name: String,
            consecutive: String,
            municipalityId: String,
            statisticTypeIds: List<String>
        ): Agency {
            val agencyId = AgencyId.random()
            val agencyName = AgencyName(name)
            val agencyConsecutive = AgencyConsecutive(consecutive)
            val agencyMunicipalityId = MunicipalityId.fromString(municipalityId)
            val agencyStatisticTypeIds = statisticTypeIds.map { StatisticTypeId.fromString(it) }
                .distinct()
                .toMutableList()

            return Agency(
                agencyId,
                agencyName,
                agencyConsecutive,
                agencyMunicipalityId,
                agencyStatisticTypeIds,
                AgencyCreateDate.now(),
                null
            )
        }

        /**
         * Initialize an entity of [Agency] from primitive values
         *
         * @param id The id of agency
         * @param name The name of agency
         * @param consecutive The consecutive key of agency
         * @param municipalityId The id of municipality where the agency is located
         * @param statisticTypeIds The ids of statistic types used to generate the summary reports
         * @param updatedAt The date of last update of agency
         * @return An entity of Agency
         */
        fun from(
            id: String,
            name: String,
            consecutive: String,
            municipalityId: String,
            statisticTypeIds: List<String>,
            createdAt: Date,
            updatedAt: Date?
        ): Agency {
            val agencyId = AgencyId.fromString(id)
            val agencyName = AgencyName(name)
            val agencyConsecutive = AgencyConsecutive(consecutive)
            val agencyMunicipalityId = MunicipalityId.fromString(municipalityId)
            val agencyStatisticTypeIds = statisticTypeIds.map { StatisticTypeId.fromString(it) }
                .distinct()
                .toMutableList()

            return Agency(
                agencyId,
                agencyName,
                agencyConsecutive,
                agencyMunicipalityId,
                agencyStatisticTypeIds.toMutableList(),
                AgencyCreateDate(createdAt),
                updatedAt?.let {
                    if (it.before(createdAt))
                        throw InvalidAgencyUpdateDateException(it, createdAt)

                    AgencyUpdateDate(it)
                }
            )
        }
    }

    fun id() = id.value

    fun name() = name.value

    fun consecutive() = consecutive.value

    fun createdAt() = createdAt.value

    fun updatedAt() = updatedAt?.value

    fun municipalityId() = municipalityId.value

    fun statisticTypeIds() = statisticTypeIds.toList()

    fun changeName(name: String) {
        this.name = AgencyName(name)
        this.updatedAt = AgencyUpdateDate.now()
    }

    fun changeConsecutive(consecutive: String) {
        this.consecutive = AgencyConsecutive(consecutive)
        this.updatedAt = AgencyUpdateDate.now()
    }

    fun changeMunicipalityId(municipalityId: String) {
        this.municipalityId = MunicipalityId.fromString(municipalityId)
        this.updatedAt = AgencyUpdateDate.now()
    }

    fun addStatisticTypeId(statisticTypeId: String) {
        val agencyStatisticTypeId = StatisticTypeId.fromString(statisticTypeId)

        statisticTypeIds.takeIf { !it.contains(agencyStatisticTypeId) }?.let {
            it.add(agencyStatisticTypeId)
            this.updatedAt = AgencyUpdateDate.now()
        }
    }

    fun removeStatisticTypeId(statisticTypeId: String) {
        val agencyStatisticTypeId = StatisticTypeId.fromString(statisticTypeId)

        statisticTypeIds.takeIf { it.contains(agencyStatisticTypeId) }?.let {
            it.remove(agencyStatisticTypeId)
            this.updatedAt = AgencyUpdateDate.now()
        }
    }

    fun replaceStatisticTypeIds(statisticTypeIds: List<String>) {
        if (statisticTypeIds.isEmpty())
            throw InvalidAgencyStatisticTypesException()

        this.statisticTypeIds.apply {
            clear()
            addAll(statisticTypeIds.map { StatisticTypeId.fromString(it) })
        }

        updatedAt = AgencyUpdateDate.now()
    }
}

data class AgencyId(val value: UUID) {
    companion object {
        fun random() = AgencyId(UUID.randomUUID())

        fun fromString(value: String) = try {
            AgencyId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidAgencyIdException(value, e)
        }
    }
}

data class AgencyName(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (value.isBlank()) {
            throw InvalidAgencyNameException(value)
        }
    }
}

data class AgencyConsecutive(val value: String) {
    init {
        validate()
    }

    private fun validate() {
        if (Regex("[0-9]{4}").matches(value).not()) {
            throw InvalidAgencyConsecutiveException(value)
        }
    }
}

data class AgencyCreateDate(val value: Date) {
    companion object {
        fun now() = AgencyCreateDate(Date())
    }
}

data class AgencyUpdateDate(val value: Date) {
    companion object {
        fun now() = AgencyUpdateDate(Date())
    }
}
