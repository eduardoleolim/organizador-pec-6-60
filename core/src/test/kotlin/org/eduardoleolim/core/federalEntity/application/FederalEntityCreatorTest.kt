package org.eduardoleolim.core.federalEntity.application

import org.eduardoleolim.core.federalEntity.infrastructure.persistence.InMemoryFederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.create.FederalEntityCreator
import org.junit.jupiter.api.BeforeEach
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

    @Test
    fun `federal entity already exists`() {
        val keyCode = "30"
        val name = "VERACRUZ"

        try {
            creator.create(keyCode, name)
            creator.create(keyCode, name)
            assert(false)
        } catch (e: Exception) {
            assert(e.message == "The federal entity with key code <$keyCode> already exists")
        }
    }
}
