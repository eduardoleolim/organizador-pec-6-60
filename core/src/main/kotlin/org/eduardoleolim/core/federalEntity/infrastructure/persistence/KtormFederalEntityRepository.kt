package org.eduardoleolim.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.core.federalEntity.domain.*
import org.eduardoleolim.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class KtormFederalEntityRepository(private val database: Database) : FederalEntityRepository {
    private val federalEntities = FederalEntities("f")

    override fun matching(criteria: Criteria): List<FederalEntity> {
        return KtormFederalEntitiesCriteriaParser.select(database, federalEntities, criteria).map {
            federalEntities.createEntity(it)
        }.map {
            FederalEntity.from(
                it.id,
                it.keyCode,
                it.name,
                Date.from(it.createdAt.atZone(ZoneId.systemDefault()).toInstant()),
                it.updatedAt?.let { date ->
                    Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
                }
            )
        }
    }

    override fun count(criteria: Criteria): Int {
        return KtormFederalEntitiesCriteriaParser.count(database, federalEntities, criteria)
            .rowSet.apply {
                next()
            }.getInt(1)
    }


    override fun save(federalEntity: FederalEntity) {
        database.useTransaction {
            database.insertOrUpdate(federalEntities) {
                set(it.id, federalEntity.id().toString())
                set(it.keyCode, federalEntity.keyCode())
                set(it.name, federalEntity.name())
                set(
                    it.createdAt,
                    federalEntity.createdAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                )
                onConflict(it.id) {
                    set(it.keyCode, federalEntity.keyCode())
                    set(it.name, federalEntity.name())
                    set(
                        it.updatedAt,
                        federalEntity.updatedAt()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                            ?: LocalDateTime.now()
                    )
                }
            }
        }
    }

    override fun delete(federalEntityId: FederalEntityId) {
        database.useTransaction {
            count(FederalEntityCriteria.idCriteria(federalEntityId.value.toString())).let { count ->
                if (count == 0)
                    throw FederalEntityNotFoundError(federalEntityId.value.toString())

                database.delete(federalEntities) {
                    it.id eq federalEntityId.toString()
                }
            }
        }
    }
}
