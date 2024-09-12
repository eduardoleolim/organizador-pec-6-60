package org.eduardoleolim.organizadorpec660.user.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.role.domain.Role
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.Credentials
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.Roles
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.Users
import org.eduardoleolim.organizadorpec660.user.domain.User
import org.eduardoleolim.organizadorpec660.user.domain.UserCriteria
import org.eduardoleolim.organizadorpec660.user.domain.UserNotFoundError
import org.eduardoleolim.organizadorpec660.user.domain.UserRepository
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
    private val criteriaParser = KtormUsersCriteriaParser(database, users, credentials, roles)

    override fun matching(criteria: Criteria): List<User> {
        val query = criteriaParser.selectQuery(criteria)

        return query.map { rowSet ->
            val credentials = credentials.createEntity(rowSet, false)
            val role = roles.createEntity(rowSet, false)
            val user = users.createEntity(rowSet, false)

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
        val query = criteriaParser.countQuery(criteria)

        return query.rowSet.let {
            it.next()
            it.getInt(1)
        }
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
            val count = count(UserCriteria.idCriteria(userId))

            if (count == 0)
                throw UserNotFoundError(userId)

            database.delete(users) {
                it.id eq userId
            }
        }
    }
}
