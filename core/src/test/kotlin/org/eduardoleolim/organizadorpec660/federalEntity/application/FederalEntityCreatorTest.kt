package org.eduardoleolim.organizadorpec660.federalEntity.application

import org.eduardoleolim.organizadorpec660.federalEntity.application.create.FederalEntityCreator
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.persistence.InMemoryFederalEntityRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FederalEntityCreatorTest {
    private val repository = InMemoryFederalEntityRepository()
    private val creator = FederalEntityCreator(repository)

    @BeforeEach
    fun beforeEach() {
        repository.records.clear()
    }

    @Test
    fun `create a federal entity`() {
        val keyCode = "30"
        val name = "VERACRUZ"

        try {
            creator.create(keyCode, name)
                .fold(
                    ifRight = {
                        assert(repository.records.size == 1)
                    },
                    ifLeft = {
                        assert(false)
                    }
                )

        } catch (e: Throwable) {
            assert(false)
        }
    }

    @Nested
    inner class ValidationTests {
        @BeforeEach
        fun beforeEach() {
            FederalEntity.create("30", "VERACRUZ").let {
                repository.records[it.id().toString()] = it
            }
        }

        @Test
        fun `fail if federal entity already exists`() {
            val keyCode = "30"
            val name = "VERACRUZ"

            try {
                creator.create(keyCode, name).fold(
                    ifRight = {
                        assert(false)
                    },
                    ifLeft = {
                        assert(it is FederalEntityAlreadyExistsError)
                    }
                )
            } catch (e: Throwable) {
                assert(false)
            }
        }
    }
}
