package org.eduardoleolim.organizadorpec660.core.federalEntity.domain

import org.eduardoleolim.organizadorpec660.federalEntity.domain.*
import org.junit.jupiter.api.Test
import java.util.*

class FederalEntityTest {
    @Test
    fun `check empty name`() {
        val name = ""
        try {
            FederalEntityName(name)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityNameError)
        }
    }

    @Test
    fun `check blank name`() {
        val name = " "
        try {
            FederalEntityName(name)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityNameError)
        }
    }

    @Test
    fun `check valid name`() {
        val name = "Federal Entity Name"
        try {
            FederalEntityName(name)
            assert(true)
        } catch (e: InvalidFederalEntityNameError) {
            assert(false)
        }
    }

    @Test
    fun `check empty key code`() {
        val keyCode = ""
        try {
            FederalEntityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityKeyCodeError)
        }
    }

    @Test
    fun `check blank key code`() {
        val keyCode = " "
        try {
            FederalEntityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityKeyCodeError)
        }
    }

    @Test
    fun `check key code with less than 2 characters`() {
        val keyCode = "1"
        try {
            FederalEntityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityKeyCodeError)
        }
    }

    @Test
    fun `check key code with more than 2 characters`() {
        val keyCode = "123"
        try {
            FederalEntityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityKeyCodeError)
        }
    }

    @Test
    fun `check key code with letters`() {
        val keyCode = "ab"
        try {
            FederalEntityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityKeyCodeError)
        }
    }

    @Test
    fun `check key code with special characters`() {
        val keyCode = "1@"
        try {
            FederalEntityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityKeyCodeError)
        }
    }

    @Test
    fun `check valid key code`() {
        val keyCode = "12"
        try {
            FederalEntityKeyCode(keyCode)
            assert(true)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityKeyCodeError)
        }
    }

    @Test
    fun `check empty id`() {
        val id = ""
        try {
            FederalEntityId.fromString(id)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityIdError)
        }
    }

    @Test
    fun `check blank id`() {
        val id = " "
        try {
            FederalEntityId.fromString(id)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityIdError)
        }
    }

    @Test
    fun `check valid id`() {
        val id = "123e4567-e89b-12d3-a456-426614174000"
        try {
            FederalEntityId.fromString(id)
            assert(true)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityIdError)
        }
    }

    @Test
    fun `check invalid id`() {
        val id = "This is not a valid id"
        try {
            FederalEntityId.fromString(id)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityIdError)
        }
    }

    @Test
    fun `check overlapping update date`() {
        val time = System.currentTimeMillis()
        val createdAt = FederalEntityCreateDate(Date(time))
        val updatedAt = FederalEntityUpdateDate(Date(time - 1))
        try {
            FederalEntity.from(
                FederalEntityId.random().value.toString(),
                "12",
                "Federal Entity Name",
                createdAt.value,
                updatedAt.value
            )
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidFederalEntityUpdateDateError)
        }
    }
}
