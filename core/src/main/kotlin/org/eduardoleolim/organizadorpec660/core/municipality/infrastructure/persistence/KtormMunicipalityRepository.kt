package org.eduardoleolim.organizadorpec660.core.municipality.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Municipalities
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
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
        database.useTransaction {
            database.insertOrUpdate(municipalities) {
                val createdAt = LocalDateTime.ofInstant(municipality.createdAt().toInstant(), ZoneId.systemDefault())
                set(it.id, municipality.id().toString())
                set(it.keyCode, municipality.keyCode())
                set(it.name, municipality.name())
                set(it.federalEntityId, municipality.federalEntityId().toString())
                set(it.createdAt, createdAt)

                onConflict(it.id) {
                    val updatedAt = municipality.updatedAt()?.let { date ->
                        LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                    } ?: LocalDateTime.now()
                    set(it.keyCode, municipality.keyCode())
                    set(it.name, municipality.name())
                    set(it.federalEntityId, municipality.federalEntityId().toString())
                    set(it.updatedAt, updatedAt)
                }
            }
        }
    }

    override fun delete(municipalityId: String) {
        database.useTransaction {
            count(MunicipalityCriteria.idCriteria(municipalityId)).let { count ->
                if (count == 0)
                    throw MunicipalityNotFoundError(municipalityId)

                database.delete(municipalities) {
                    it.id eq municipalityId
                }
            }
        }
    }
}
