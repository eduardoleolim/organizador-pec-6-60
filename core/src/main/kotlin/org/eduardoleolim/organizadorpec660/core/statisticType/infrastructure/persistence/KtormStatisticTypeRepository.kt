package org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.InstrumentTypesOfStatisticTypes
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.StatisticTypes
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository
import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime
import java.time.ZoneId

class KtormStatisticTypeRepository(private val database: Database) : StatisticTypeRepository {
    private val statisticTypes = StatisticTypes("st")
    private val instrumentTypesOfStatisticTypes = InstrumentTypesOfStatisticTypes("st_it")

    override fun matching(criteria: org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria): List<StatisticType> {
        TODO("Not yet implemented")
    }

    override fun count(criteria: org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria): Int {
        TODO("Not yet implemented")
    }

    override fun save(statisticType: StatisticType) {
        database.useTransaction {
            database.insertOrUpdate(statisticTypes) {
                val createdAt = LocalDateTime.ofInstant(statisticType.createdAt().toInstant(), ZoneId.systemDefault())
                set(statisticTypes.id, statisticType.id().toString())
                set(statisticTypes.keyCode, statisticType.keyCode())
                set(statisticTypes.name, statisticType.name())
                set(statisticTypes.createdAt, createdAt)

                onConflict(statisticTypes.id) {
                    val updatedAt = statisticType.updatedAt()?.let { date ->
                        LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                    } ?: LocalDateTime.now()
                    set(statisticTypes.keyCode, statisticType.keyCode())
                    set(statisticTypes.name, statisticType.name())
                    set(statisticTypes.updatedAt, updatedAt)
                }
            }

            database.delete(instrumentTypesOfStatisticTypes) {
                it.statisticTypeId eq statisticType.id().toString()
            }

            database.batchInsert(instrumentTypesOfStatisticTypes) {
                statisticType.instrumentTypeIds().forEach { instrumentTypeId ->
                    item {
                        set(it.statisticTypeId, statisticType.id().toString())
                        set(it.instrumentTypeId, instrumentTypeId.toString())
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
