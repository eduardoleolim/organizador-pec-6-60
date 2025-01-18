/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
