package org.eduardoleolim.organizadorpec660.municipality.application

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.persistence.InMemoryFederalEntityRepository
import org.eduardoleolim.organizadorpec660.municipality.application.create.MunicipalityCreator
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.municipality.infrastructure.InMemoryMunicipalityRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MunicipalityCreatorTest {
    private val municipalityRepository = InMemoryMunicipalityRepository()
    private val federalEntityRepository = InMemoryFederalEntityRepository()
    private val creator = MunicipalityCreator(municipalityRepository, federalEntityRepository)

    private val aguascalientesId = "ae7434f1-a8d5-4300-971b-1ec04a701e57"
    private val bajaCaliforniaId = "ae7434f1-a8d5-4300-971b-1ec04a701e58"
    private val bajaCaliforniaSurId = "ae7434f1-a8d5-4300-971b-1ec04a701e59"
    private val campecheId = "ae7434f1-a8d5-4300-971b-1ec04a701e60"
    private val veracruzId = "ae7434f1-a8d5-4300-971b-1ec04a701e61"

    @BeforeEach
    fun beforeEach() {
        municipalityRepository.municipalities.clear()
        municipalityRepository.federalEntities.clear()
        federalEntityRepository.records.clear()

        listOf(
            FederalEntity.from(aguascalientesId, "01", "AGUASCALIENTES", Date(), null),
            FederalEntity.from(bajaCaliforniaId, "02", "BAJA CALIFORNIA", Date(), null),
            FederalEntity.from(bajaCaliforniaSurId, "03", "BAJA CALIFORNIA SUR", Date(), null),
            FederalEntity.from(campecheId, "04", "CAMPECHE", Date(), null),
            FederalEntity.from(veracruzId, "30", "VERACRUZ", Date(), null)
        ).forEach {
            val key = it.id().toString()

            federalEntityRepository.records[key] = it
            municipalityRepository.federalEntities[key] = it
        }
    }

    @Test
    fun `create a municipality`() {
        val keyCode = "001"
        val name = "AGUASCALIENTES"
        val federalEntityId = aguascalientesId

        try {
            creator.create(keyCode, name, federalEntityId)
            assert(municipalityRepository.municipalities.size == 1)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Nested
    inner class ValidationTests {
        private val municipalityId = "ae7434f1-a8d5-4300-971b-1ec04a701e62"

        @BeforeEach
        fun beforeEach() {
            Municipality.from(municipalityId, "001", "AGUASCALIENTES", aguascalientesId, Date(), null).let {
                municipalityRepository.municipalities[it.id().toString()] = it
            }
        }

        @Test
        fun `fails if municipality already exists`() {
            val keyCode = "001"
            val name = "AGUASCALIENTES"
            val federalEntityId = aguascalientesId

            try {
                creator.create(keyCode, name, federalEntityId)
                    .fold(
                        ifRight = {
                            assert(false)
                        },
                        ifLeft = {
                            assert(it is MunicipalityAlreadyExistsError)
                        }
                    )
            } catch (e: Throwable) {
                assert(false)
            }
        }

        @Test
        fun `create with same keyCode but different federal entity`() {
            val keyCode = "001"
            val name = "ACAJETE"
            val federalEntityId = veracruzId

            try {
                creator.create(keyCode, name, federalEntityId)
                    .fold(
                        ifRight = {
                            assert(municipalityRepository.municipalities.size == 2)
                        },
                        ifLeft = {
                            assert(false)
                        }
                    )
            } catch (e: Exception) {
                assert(false)
            }
        }
    }
}
