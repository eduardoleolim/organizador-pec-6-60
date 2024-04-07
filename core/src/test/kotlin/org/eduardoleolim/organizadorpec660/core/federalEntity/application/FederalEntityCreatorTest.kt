package org.eduardoleolim.organizadorpec660.core.federalEntity.application

import org.eduardoleolim.organizadorpec660.core.federalEntity.application.create.FederalEntityCreator
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.persistence.InMemoryFederalEntityRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FederalEntityCreatorTest {
    private val repository = InMemoryFederalEntityRepository()
    private val creator = FederalEntityCreator(repository)

    @BeforeEach
    fun before() {
        repository.records.clear()
    }

    @Test
    fun `create a federal entity`() {
        val keyCode = "30"
        val name = "VERACRUZ"

        try {
            creator.create(keyCode, name)
            assert(repository.records.size == 1)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Nested
    inner class ValidationTest {
        @BeforeEach
        fun before() {
            FederalEntity.create("30", "VERACRUZ").let {
                repository.records[it.id().toString()] = it
            }
        }

        @Test
        fun `federal entity already exists`() {
            val keyCode = "30"
            val name = "VERACRUZ"

            try {
                creator.create(keyCode, name)
                assert(false)
            } catch (e: FederalEntityAlreadyExistsError) {
                assert(e.message == "The federal entity with key code <$keyCode> already exists")
            }
        }
    }
}
