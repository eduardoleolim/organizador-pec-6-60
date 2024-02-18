package org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.persistence

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorPec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorPec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.InstrumentTypes
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.InstrumentTypesOfStatisticTypes
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.StatisticTypes
import org.eduardoleolim.organizadorPec660.core.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorPec660.core.statisticType.domain.StatisticTypeRepository
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormStatisticTypeRepository(private val database: Database) : StatisticTypeRepository {
    private val statisticTypes = StatisticTypes("st")
    private val instrumentTypes = InstrumentTypes("it")
    private val instrumentTypesOfStatisticTypes = InstrumentTypesOfStatisticTypes("st_it")

    override fun matching(criteria: Criteria): List<StatisticType> {
        return KtormStatisticTypeCriteriaParser.select(
            database,
            statisticTypes,
            instrumentTypes,
            instrumentTypesOfStatisticTypes,
            criteria
        ).map { rowSet ->
            statisticTypes.createEntity(rowSet).let {
                val instrumentTypeIds = database.from(instrumentTypesOfStatisticTypes)
                    .select()
                    .where { instrumentTypesOfStatisticTypes.statisticTypeId eq it.id }
                    .map { rowSet ->
                        instrumentTypesOfStatisticTypes.createEntity(rowSet).instrumentType.id
                    }.toMutableList()

                StatisticType.from(
                    it.id,
                    it.keyCode,
                    it.name,
                    instrumentTypeIds,
                    it.createdAt.toDate(),
                    it.updatedAt?.toDate()
                )
            }
        }
    }

    override fun count(criteria: Criteria): Int {
        return KtormStatisticTypeCriteriaParser.count(
            database,
            statisticTypes,
            instrumentTypes,
            instrumentTypesOfStatisticTypes,
            criteria
        ).rowSet.apply {
            next()
        }.getInt(1)
    }

    override fun save(statisticType: StatisticType) {
        database.useTransaction {
            database.insertOrUpdate(statisticTypes) {
                set(statisticTypes.id, statisticType.id().toString())
                set(statisticTypes.keyCode, statisticType.keyCode())
                set(statisticTypes.name, statisticType.name())
                set(statisticTypes.createdAt, statisticType.createdAt().toLocalDateTime())

                onConflict(statisticTypes.id) {
                    set(statisticTypes.keyCode, statisticType.keyCode())
                    set(statisticTypes.name, statisticType.name())
                    set(statisticTypes.updatedAt, statisticType.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                }
            }

            database.delete(instrumentTypesOfStatisticTypes) {
                it.statisticTypeId eq statisticType.id().toString()
            }

            database.batchInsert(instrumentTypesOfStatisticTypes) {
                statisticType.instrumentTypeIds().forEach { instrumentTypeId ->
                    item {
                        set(it.statisticTypeId, statisticType.id().toString())
                        set(it.instrumentTypeId, instrumentTypeId)
                    }
                }
            }
        }
    }

    override fun delete(statisticTypeId: String) {
        database.useTransaction {
            database.delete(instrumentTypesOfStatisticTypes) {
                it.statisticTypeId eq statisticTypeId
            }

            database.delete(statisticTypes) {
                it.id eq statisticTypeId
            }
        }
    }
}
