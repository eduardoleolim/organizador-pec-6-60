package org.eduardoleolim.organizadorpec660.user.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Credentials
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Roles
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Users
import org.eduardoleolim.organizadorpec660.user.domain.UserFields
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

class KtormUsersCriteriaParser(
    private val database: Database,
    private val users: Users,
    private val credentials: Credentials,
    private val roles: Roles
) {
    fun selectQuery(criteria: Criteria): Query {
        return database.from(users)
            .innerJoin(credentials, on = users.id eq credentials.userId)
            .innerJoin(roles, on = users.roleId eq roles.id)
            .select().let {
                addOrdersToQuery(it, criteria)
            }.let {
                addConditionsToQuery(it, criteria)
            }.limit(criteria.limit, criteria.offset)
    }

    fun countQuery(criteria: Criteria): Query {
        val baseQuery = selectQuery(criteria)
        val querySource = QuerySource(database, users, baseQuery.expression)

        return querySource.select(count())
    }

    private fun addOrdersToQuery(query: Query, criteria: Criteria): Query {
        if (criteria.hasOrders().not())
            return query

        return query.orderBy(criteria.orders.orders.mapNotNull { parseOrder(it) })
    }

    private fun parseOrder(order: Order): OrderByExpression? {
        val orderBy = order.orderBy.value
        val orderType = order.orderType
        val field = UserFields.entries.firstOrNull { it.value == orderBy }
        val column = when (field) {
            UserFields.Id -> users.id
            UserFields.Firstname -> users.firstname
            UserFields.Lastname -> users.lastname
            UserFields.Email -> credentials.email
            UserFields.Username -> credentials.username
            UserFields.RoleId -> roles.id
            UserFields.RoleName -> roles.name
            UserFields.CreatedAt -> users.createdAt
            UserFields.UpdatedAt -> users.updatedAt
            null -> throw InvalidArgumentError()
        }

        return parseOrderType(orderType, column)
    }

    private fun parseOrderType(orderType: OrderType, column: Column<*>): OrderByExpression? {
        return when (orderType) {
            OrderType.ASC -> column.asc()
            OrderType.DESC -> column.desc()
            OrderType.NONE -> null
        }
    }

    private fun addConditionsToQuery(query: Query, criteria: Criteria): Query {
        criteria.filters.let {
            return when (it) {
                is EmptyFilters -> query

                is SingleFilter -> parseFilter(it.filter)?.let { conditions ->
                    query.where(conditions)
                } ?: query

                is MultipleFilters -> parseMultipleFilters(it)?.let { conditions ->
                    query.where(conditions)
                } ?: query
            }
        }
    }

    private fun parseMultipleFilters(filters: MultipleFilters): ColumnDeclaring<Boolean>? {
        if (filters.isEmpty())
            return null

        val filterConditions = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> parseFilter(it.filter)
                is MultipleFilters -> parseMultipleFilters(it)
                else -> null
            }
        }

        return when (filters.operator) {
            FiltersOperator.AND -> filterConditions.reduce { left, right -> left and right }

            FiltersOperator.OR -> filterConditions.reduce { left, right -> left or right }
        }
    }

    private fun parseFilter(filter: Filter): ColumnDeclaring<Boolean>? {
        val field = UserFields.entries.firstOrNull { it.value == filter.field.value }
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            UserFields.Id -> {
                when (operator) {
                    FilterOperator.EQUAL -> users.id eq value
                    FilterOperator.NOT_EQUAL -> users.id notEq value
                    else -> null
                }
            }

            UserFields.Firstname -> {
                when (operator) {
                    FilterOperator.EQUAL -> users.firstname eq value
                    FilterOperator.NOT_EQUAL -> users.firstname notEq value
                    FilterOperator.CONTAINS -> users.firstname like "%$value%"
                    FilterOperator.NOT_CONTAINS -> users.firstname notLike "%$value%"
                    else -> null
                }
            }

            UserFields.Lastname -> {
                when (operator) {
                    FilterOperator.EQUAL -> users.lastname eq value
                    FilterOperator.NOT_EQUAL -> users.lastname notEq value
                    FilterOperator.CONTAINS -> users.lastname like "%$value%"
                    FilterOperator.NOT_CONTAINS -> users.lastname notLike "%$value%"
                    else -> null
                }
            }

            UserFields.Email -> {
                when (operator) {
                    FilterOperator.EQUAL -> credentials.email eq value
                    FilterOperator.NOT_EQUAL -> credentials.email notEq value
                    FilterOperator.CONTAINS -> credentials.email like "%$value%"
                    FilterOperator.NOT_CONTAINS -> credentials.email notLike "%$value%"
                    else -> null
                }
            }

            UserFields.Username -> {
                when (operator) {
                    FilterOperator.EQUAL -> credentials.username eq value
                    FilterOperator.NOT_EQUAL -> credentials.username notEq value
                    FilterOperator.CONTAINS -> credentials.username like "%$value%"
                    FilterOperator.NOT_CONTAINS -> credentials.username notLike "%$value%"
                    else -> null
                }
            }

            UserFields.CreatedAt -> {
                val date = LocalDateTime.from(Instant.parse(value))

                when (operator) {
                    FilterOperator.EQUAL -> users.createdAt eq date
                    FilterOperator.NOT_EQUAL -> users.createdAt notEq date
                    FilterOperator.GT -> users.createdAt greater date
                    FilterOperator.GTE -> users.createdAt greaterEq date
                    FilterOperator.LT -> users.createdAt less date
                    FilterOperator.LTE -> users.createdAt lessEq date
                    else -> null
                }
            }

            UserFields.UpdatedAt -> {
                val date = LocalDateTime.from(Instant.parse(value))

                when (operator) {
                    FilterOperator.EQUAL -> users.updatedAt eq date
                    FilterOperator.NOT_EQUAL -> users.updatedAt notEq date
                    FilterOperator.GT -> users.updatedAt greater date
                    FilterOperator.GTE -> users.updatedAt greaterEq date
                    FilterOperator.LT -> users.updatedAt less date
                    FilterOperator.LTE -> users.updatedAt lessEq date
                    else -> null
                }
            }

            UserFields.RoleId -> {
                when (operator) {
                    FilterOperator.EQUAL -> roles.id eq value
                    FilterOperator.NOT_EQUAL -> roles.id notEq value
                    else -> null
                }
            }

            UserFields.RoleName -> {
                when (operator) {
                    FilterOperator.EQUAL -> roles.name eq value
                    FilterOperator.NOT_EQUAL -> roles.name notEq value
                    FilterOperator.CONTAINS -> roles.name like "%$value%"
                    FilterOperator.NOT_CONTAINS -> roles.name notLike "%$value%"
                    else -> null
                }
            }

            else -> throw InvalidArgumentError()
        }
    }
}
