package org.eduardoleolim.organizadorpec660.core.user.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.role.domain.Role
import org.eduardoleolim.organizadorpec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Credentials
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Roles
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Users
import org.eduardoleolim.organizadorpec660.core.user.domain.User
import org.eduardoleolim.organizadorpec660.core.user.domain.UserCriteria
import org.eduardoleolim.organizadorpec660.core.user.domain.UserNotFoundError
import org.eduardoleolim.organizadorpec660.core.user.domain.UserRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormUserRepository(private val database: Database) : UserRepository {
    private val users = Users("u")
    private val credentials = Credentials("c")
    private val roles = Roles("r")

    override fun matching(criteria: Criteria): List<User> {
        return KtormUsersCriteriaParser.select(database, users, credentials, roles, criteria).map {
            val credentials = credentials.createEntity(it, false)
            val role = roles.createEntity(it, false)
            val user = users.createEntity(it, false)

            User.from(
                user.id,
                user.firstname,
                user.lastname,
                credentials.email,
                credentials.username,
                credentials.password,
                Role.from(role.id, role.name),
                user.createdAt.toDate(),
                user.updatedAt?.toDate()
            )
        }
    }

    override fun count(criteria: Criteria): Int {
        return KtormUsersCriteriaParser.count(database, users, credentials, roles, criteria)
            .rowSet.apply {
                next()
            }.getInt(1)
    }

    override fun save(user: User) {
        database.useTransaction {
            database.insertOrUpdate(users) {
                set(it.id, user.id().toString())
                set(it.firstname, user.firstName())
                set(it.lastname, user.lastName())
                set(it.roleId, user.role().id().toString())
                set(it.createdAt, user.createdAt().toLocalDateTime())

                onConflict(it.id) {
                    set(it.firstname, user.firstName())
                    set(it.lastname, user.lastName())
                    set(it.roleId, user.role().id().toString())
                    set(it.updatedAt, user.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                }
            }

            database.insertOrUpdate(credentials) {
                set(it.userId, user.id().toString())
                set(it.email, user.email())
                set(it.username, user.username())
                set(it.password, user.password())

                onConflict(it.userId) {
                    set(it.email, user.email())
                    set(it.username, user.username())
                    set(it.password, user.password())
                }
            }
        }
    }

    override fun delete(userId: String) {
        database.useTransaction {
            count(UserCriteria.idCriteria(userId)).let { count ->
                if (count == 0)
                    throw UserNotFoundError(userId)

                database.delete(users) {
                    it.id eq userId
                }
            }
        }
    }
}
