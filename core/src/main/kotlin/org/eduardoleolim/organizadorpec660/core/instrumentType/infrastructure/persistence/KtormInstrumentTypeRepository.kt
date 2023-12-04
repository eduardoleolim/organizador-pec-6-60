package org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.InstrumentTypes
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormInstrumentTypeRepository(private val database: Database) : InstrumentTypeRepository {
    private val instrumentTypes = InstrumentTypes("it")

    override fun matching(criteria: Criteria): List<InstrumentType> {
        return KtormInstrumentTypesCriteriaParser.select(database, instrumentTypes, criteria).map { rowSet ->
            instrumentTypes.createEntity(rowSet).let {
                InstrumentType.from(it.id, it.name, it.createdAt.toDate(), it.updatedAt?.toDate())
            }
        }
    }

    override fun count(criteria: Criteria): Int {
        return KtormInstrumentTypesCriteriaParser.count(database, instrumentTypes, criteria)
            .rowSet.let {
                it.next()
                it.getInt(1)
            }
    }

    override fun save(instrumentType: InstrumentType) {
        database.useTransaction {
            database.insertOrUpdate(instrumentTypes) {
                set(it.id, instrumentType.id().toString())
                set(it.name, instrumentType.name())
                set(it.createdAt, instrumentType.createdAt().toLocalDateTime())

                onConflict(it.id) {
                    set(it.name, instrumentType.name())
                    set(it.updatedAt, instrumentType.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
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
