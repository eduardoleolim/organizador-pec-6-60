package org.eduardoleolim.organizadorpec660.core.municipality.domain

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityId
import org.junit.jupiter.api.Test
import java.util.*

class MunicipalityTest {
    @Test
    fun `check empty name`() {
        val name = ""
        try {
            MunicipalityName(name)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityNameError)
        }
    }

    @Test
    fun `check blank name`() {
        val name = " "
        try {
            MunicipalityName(name)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityNameError)
        }
    }

    @Test
    fun `check valid name`() {
        val name = "Municipality Name"
        try {
            MunicipalityName(name)
            assert(true)
        } catch (e: InvalidMunicipalityNameError) {
            assert(false)
        }
    }

    @Test
    fun `check empty key code`() {
        val keyCode = ""
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityKeyCodeError)
        }
    }

    @Test
    fun `check blank key code`() {
        val keyCode = " "
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityKeyCodeError)
        }
    }

    @Test
    fun `check key code with less than 3 characters`() {
        val keyCode = "12"
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityKeyCodeError)
        }
    }

    @Test
    fun `check key code with more than 3 characters`() {
        val keyCode = "1234"
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityKeyCodeError)
        }
    }

    @Test
    fun `check key code with letters`() {
        val keyCode = "abc"
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityKeyCodeError)
        }
    }

    @Test
    fun `check key code with special characters`() {
        val keyCode = "%&@"
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityKeyCodeError)
        }
    }

    @Test
    fun `check valid key code`() {
        val keyCode = "123"
        try {
            MunicipalityKeyCode(keyCode)
            assert(true)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityKeyCodeError)
        }
    }

    @Test
    fun `check empty id`() {
        val id = ""
        try {
            MunicipalityId.fromString(id)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityIdError)
        }
    }

    @Test
    fun `check blank id`() {
        val id = " "
        try {
            MunicipalityId.fromString(id)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityIdError)
        }
    }

    @Test
    fun `check valid id`() {
        val id = "123e4567-e89b-12d3-a456-426614174000"
        try {
            MunicipalityId.fromString(id)
            assert(true)
        } catch (e: Throwable) {
            assert(false)
        }
    }

    @Test
    fun `check invalid id`() {
        val id = "This is not a valid id"
        try {
            MunicipalityId.fromString(id)
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityIdError)
        }
    }

    @Test
    fun `check overlapping update date`() {
        val time = System.currentTimeMillis()
        val createdAt = MunicipalityCreateDate(Date(time))
        val updatedAt = MunicipalityUpdateDate(Date(time - 1))
        try {
            Municipality.from(
                MunicipalityId.random().value.toString(),
                "123",
                "Municipality Name",
                FederalEntityId.random().value.toString(),
                createdAt.value,
                updatedAt.value
            )
            assert(false)
        } catch (e: Throwable) {
            assert(e is InvalidMunicipalityUpdateDateError)
        }
    }
}
