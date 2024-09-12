package org.eduardoleolim.organizadorpec660.role.domain

import java.util.*

class Role private constructor(
    private val id: RoleId,
    private val name: RoleName
) {
    companion object {
        fun from(id: String, name: String) = Role(
            RoleId.fromString(id),
            RoleName(name)
        )
    }

    fun id() = id.value

    fun name() = name.value
}

data class RoleId(val value: UUID) {
    companion object {
        fun random() = RoleId(UUID.randomUUID())

        fun fromString(value: String) = try {
            RoleId(UUID.fromString(value))
        } catch (e: Exception) {
            throw InvalidRoleIdException(value, e)
        }
    }
}

data class RoleName(val value: String)
