package org.eduardoleolim.core.instrumentType.infrastructure.persistence

import org.eduardoleolim.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeNotFoundError
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeRepository
import org.eduardoleolim.core.shared.infrastructure.models.InstrumentTypes
import org.eduardoleolim.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class KtormInstrumentTypeRepository(private val database: Database) : InstrumentTypeRepository {
    private val instrumentTypes = InstrumentTypes("it")

    override fun matching(criteria: Criteria): List<InstrumentType> {
        return KtormInstrumentTypesCriteriaParser.select(database, instrumentTypes, criteria).map {
            instrumentTypes.createEntity(it)
        }.map {
            InstrumentType.from(
                it.id,
                it.name,
                Date.from(it.createdAt.atZone(ZoneId.systemDefault()).toInstant()),
                it.updatedAt?.let { date ->
                    Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
                }
            )
        }
    }

    override fun count(criteria: Criteria): Int {
        return KtormInstrumentTypesCriteriaParser.count(database, instrumentTypes, criteria)
            .rowSet.apply {
                next()
            }.getInt(1)
    }

    override fun save(instrumentType: InstrumentType) {
        database.insertOrUpdate(instrumentTypes) {
            set(it.id, instrumentType.id().toString())
            set(it.name, instrumentType.name())
            set(it.createdAt, instrumentType.createdAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            onConflict(it.id) {
                set(it.name, instrumentType.name())
                set(
                    it.updatedAt,
                    instrumentType.updatedAt()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                        ?: LocalDateTime.now()
                )
            }
        }
    }

    override fun delete(instrumentTypeId: String) {
        count(InstrumentTypeCriteria.idCriteria(instrumentTypeId)).let { count ->
            if (count == 0)
                throw InstrumentTypeNotFoundError(instrumentTypeId)

            database.delete(instrumentTypes) {
                it.id eq instrumentTypeId
            }
        }
    }
}
