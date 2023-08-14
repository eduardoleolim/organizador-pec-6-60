package org.eduardoleolim.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.core.federalEntity.domain.FederalEntityId
import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.ZoneId
import java.util.*

class KtormFederalEntityRepository(private val database: Database) : FederalEntityRepository {
    override fun matching(criteria: Criteria): List<FederalEntity> {
        return KtormFederalEntitiesCriteriaParser.select(database, criteria).map {
            FederalEntities.createEntity(it)
        }.map {
            FederalEntity.from(
                it.id.toString(),
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
        return KtormFederalEntitiesCriteriaParser.count(database, criteria)
            .rowSet.apply {
                next()
            }.getInt(1)
    }


    override fun save(federalEntity: FederalEntity) {
        database.from(FederalEntities).select().where(FederalEntities.id eq federalEntity.id()).let {
            if (it.rowSet.size() > 0) {
                this.update(federalEntity)
            } else {
                this.insert(federalEntity)
            }
        }
    }

    private fun insert(federalEntity: FederalEntity) {
        database.insert(FederalEntities) {
            set(it.id, federalEntity.id())
            set(it.keyCode, federalEntity.keyCode())
            set(it.name, federalEntity.name())
            set(it.createdAt, federalEntity.createdAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            federalEntity.updatedAt()?.let { date ->
                set(it.updatedAt, date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            }
        }
    }

    private fun update(federalEntity: FederalEntity) {
        database.update(FederalEntities) {
            set(it.keyCode, federalEntity.keyCode())
            set(it.name, federalEntity.name())
            federalEntity.updatedAt()?.let { date ->
                set(it.updatedAt, date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            }
            where {
                it.id eq federalEntity.id()
            }
        }
    }

    override fun delete(federalEntityId: FederalEntityId) {
        database.delete(FederalEntities) {
            it.id eq federalEntityId.value
        }
    }
}
