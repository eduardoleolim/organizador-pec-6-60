package org.eduardoleolim.organizadorPec660.core.municipality.domain

import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityId
import org.junit.jupiter.api.Test
import java.util.*

class MunicipalityTest {
    @Test
    fun `check empty name`() {
        val name = ""
        try {
            MunicipalityName(name)
            assert(false)
        } catch (e: InvalidMunicipalityNameError) {
            assert(e.message == "The name <$name> is not a valid municipality name")
        }
    }

    @Test
    fun `check blank name`() {
        val name = " "
        try {
            MunicipalityName(name)
            assert(false)
        } catch (e: InvalidMunicipalityNameError) {
            assert(e.message == "The name <$name> is not a valid municipality name")
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
        } catch (e: InvalidMunicipalityKeyCodeError) {
            assert(e.message == "The key code <$keyCode> is not a valid municipality key code")
        }
    }

    @Test
    fun `check blank key code`() {
        val keyCode = " "
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: InvalidMunicipalityKeyCodeError) {
            assert(e.message == "The key code <$keyCode> is not a valid municipality key code")
        }
    }

    @Test
    fun `check key code with less than 3 characters`() {
        val keyCode = "12"
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: InvalidMunicipalityKeyCodeError) {
            assert(e.message == "The key code <$keyCode> is not a valid municipality key code")
        }
    }

    @Test
    fun `check key code with more than 3 characters`() {
        val keyCode = "1234"
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: InvalidMunicipalityKeyCodeError) {
            assert(e.message == "The key code <$keyCode> is not a valid municipality key code")
        }
    }

    @Test
    fun `check key code with letters`() {
        val keyCode = "abc"
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: InvalidMunicipalityKeyCodeError) {
            assert(e.message == "The key code <$keyCode> is not a valid municipality key code")
        }
    }

    @Test
    fun `check key code with special characters`() {
        val keyCode = "%&@"
        try {
            MunicipalityKeyCode(keyCode)
            assert(false)
        } catch (e: InvalidMunicipalityKeyCodeError) {
            assert(e.message == "The key code <$keyCode> is not a valid municipality key code")
        }
    }

    @Test
    fun `check valid key code`() {
        val keyCode = "123"
        try {
            MunicipalityKeyCode(keyCode)
            assert(true)
        } catch (e: InvalidMunicipalityKeyCodeError) {
            assert(e.message == "The key code <$keyCode> is not a valid municipality key code")
        }
    }

    @Test
    fun `check empty id`() {
        val id = ""
        try {
            MunicipalityId.fromString(id)
            assert(false)
        } catch (e: InvalidMunicipalityIdError) {
            assert(e.message == "The id <$id> is not a valid municipality id")
        }
    }

    @Test
    fun `check blank id`() {
        val id = " "
        try {
            MunicipalityId.fromString(id)
            assert(false)
        } catch (e: InvalidMunicipalityIdError) {
            assert(e.message == "The id <$id> is not a valid municipality id")
        }
    }

    @Test
    fun `check valid id`() {
        val id = "123e4567-e89b-12d3-a456-426614174000"
        try {
            MunicipalityId.fromString(id)
            assert(true)
        } catch (e: InvalidMunicipalityIdError) {
            assert(false)
        }
    }

    @Test
    fun `check invalid id`() {
        val id = "This is not a valid id"
        try {
            MunicipalityId.fromString(id)
            assert(false)
        } catch (e: InvalidMunicipalityIdError) {
            assert(e.message == "The id <$id> is not a valid municipality id")
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
        } catch (e: InvalidMunicipalityUpdateDateError) {
            assert(e.message == "The update date <${updatedAt.value}> is not valid because it is before the create date <${createdAt.value}>")
        }
    }
}
