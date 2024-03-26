package org.eduardoleolim.organizadorPec660.core.municipality.infrastructure.persistence

import org.eduardoleolim.organizadorPec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorPec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorPec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.Municipalities
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormMunicipalityRepository(private val database: Database) : MunicipalityRepository {
    private val municipalities = Municipalities("m")
    private val federalEntities = FederalEntities("f")

    override fun matching(criteria: Criteria): List<Municipality> {
        return KtormMunicipalitiesCriteriaParser.parse(database, municipalities, federalEntities, criteria)
            .map { rowSet ->
                municipalities.createEntity(rowSet).let {
                    Municipality.from(
                        it.id,
                        it.keyCode,
                        it.name,
                        it.federalEntity.id,
                        it.createdAt.toDate(),
                        it.updatedAt?.toDate()
                    )
                }
            }
    }

    override fun count(criteria: Criteria): Int {
        return KtormMunicipalitiesCriteriaParser.parse(
            database,
            municipalities,
            federalEntities,
            criteria
        ).totalRecordsInAllPages
    }

    override fun save(municipality: Municipality) {
        database.useTransaction {
            database.insertOrUpdate(municipalities) {
                set(it.id, municipality.id().toString())
                set(it.keyCode, municipality.keyCode())
                set(it.name, municipality.name())
                set(it.federalEntityId, municipality.federalEntityId().toString())
                set(it.createdAt, municipality.createdAt().toLocalDateTime())

                onConflict(it.id) {
                    set(it.keyCode, municipality.keyCode())
                    set(it.name, municipality.name())
                    set(it.federalEntityId, municipality.federalEntityId().toString())
                    set(it.updatedAt, municipality.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
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
