package org.eduardoleolim.core.municipality.infrastructure.persistence

import org.eduardoleolim.core.municipality.domain.Municipality
import org.eduardoleolim.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.core.shared.infrastructure.models.Municipalities
import org.eduardoleolim.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class KtormMunicipalityRepository(private val database: Database) : MunicipalityRepository {
    private val municipalities = Municipalities("m")
    private val federalEntities = FederalEntities("f")

    override fun matching(criteria: Criteria): List<Municipality> {
        return KtormMunicipalitiesCriteriaParser.select(database, municipalities, federalEntities, criteria).map {
            municipalities.createEntity(it)
        }.map {
            Municipality.from(
                it.id,
                it.keyCode,
                it.name,
                it.federalEntity.id,
                Date.from(it.createdAt.atZone(ZoneId.systemDefault()).toInstant()),
                it.updatedAt?.let { date ->
                    Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
                }
            )
        }
    }

    override fun count(criteria: Criteria): Int {
        return KtormMunicipalitiesCriteriaParser.count(database, municipalities, federalEntities, criteria)
            .rowSet.apply {
                next()
            }.getInt(1)
    }

    override fun save(municipality: Municipality) {
        database.insertOrUpdate(municipalities) {
            set(it.id, municipality.id().toString())
            set(it.keyCode, municipality.keyCode())
            set(it.name, municipality.name())
            set(it.federalEntityId, municipality.federalEntityId().toString())
            set(it.createdAt, municipality.createdAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            onConflict(it.id) {
                set(it.keyCode, municipality.keyCode())
                set(it.name, municipality.name())
                set(it.federalEntityId, municipality.federalEntityId().toString())
                set(
                    it.updatedAt,
                    municipality.updatedAt()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                        ?: LocalDateTime.now()
                )
            }
        }
    }

    override fun delete(municipalityId: String) {
        this.count(MunicipalityCriteria.idCriteria(municipalityId)).let { count ->
            if (count == 0)
                throw MunicipalityNotFoundError(municipalityId)

            database.delete(municipalities) {
                it.id eq municipalityId
            }
        }
    }
}
