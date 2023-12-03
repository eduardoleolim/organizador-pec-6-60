package org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeRepository
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.InstrumentTypes
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
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
        database.useTransaction {
            database.insertOrUpdate(instrumentTypes) {
                val createdAt = LocalDateTime.ofInstant(instrumentType.createdAt().toInstant(), ZoneId.systemDefault())
                set(it.id, instrumentType.id().toString())
                set(it.name, instrumentType.name())
                set(it.createdAt, createdAt)

                onConflict(it.id) {
                    val updatedAt = instrumentType.updatedAt()?.let { date ->
                        LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                    } ?: LocalDateTime.now()
                    set(it.name, instrumentType.name())
                    set(it.updatedAt, updatedAt)
                }
            }
        }
    }

    override fun delete(instrumentTypeId: String) {
        database.useTransaction {
            count(InstrumentTypeCriteria.idCriteria(instrumentTypeId)).let { count ->
                if (count == 0)
                    throw InstrumentTypeNotFoundError(instrumentTypeId)

                database.delete(instrumentTypes) {
                    it.id eq instrumentTypeId
                }
            }
        }
    }
}
