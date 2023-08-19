package org.eduardoleolim.core.municipality.infrastructure.persistence

import org.eduardoleolim.core.municipality.domain.*
import org.eduardoleolim.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.core.shared.infrastructure.models.Municipalities
import org.eduardoleolim.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.*
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
        this.count(MunicipalityCriteria.idCriteria(municipality.id().toString())).let { count ->
            if (count > 0) {
                this.update(municipality)
            } else {
                this.insert(municipality)
            }
        }
    }

    private fun insert(municipality: Municipality) {
        database.insert(municipalities) {
            set(it.id, municipality.id().toString())
            set(it.name, municipality.name())
            set(it.federalEntityId, municipality.federalEntityId().toString())
            set(it.createdAt, municipality.createdAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        }
    }

    private fun update(municipality: Municipality) {
        database.update(municipalities) {
            set(it.name, municipality.name())
            set(it.federalEntityId, municipality.federalEntityId().toString())
            set(it.updatedAt, municipality.updatedAt()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime())
            where {
                it.id eq municipality.id().toString()
            }
        }
    }

    override fun delete(municipalityId: MunicipalityId) {
        this.count(MunicipalityCriteria.idCriteria(municipalityId.value.toString())).let { count ->
            if (count == 0)
                throw MunicipalityNotFoundError(municipalityId.value.toString())

            database.delete(municipalities) {
                it.id eq municipalityId.value.toString()
            }
        }
    }
}
