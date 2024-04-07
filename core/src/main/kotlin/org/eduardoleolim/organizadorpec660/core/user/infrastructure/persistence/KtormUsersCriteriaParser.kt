package org.eduardoleolim.organizadorpec660.core.user.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Credentials
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Roles
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Users
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

object KtormUsersCriteriaParser {
    fun select(database: Database, users: Users, credentials: Credentials, roles: Roles, criteria: Criteria): Query {
        return database.from(users)
            .innerJoin(credentials, on = users.id eq credentials.userId)
            .innerJoin(roles, on = users.roleId eq roles.id)
            .select().let {
                addOrdersToQuery(it, users, credentials, roles, criteria)
            }.let {
                addConditionsToQuery(it, users, credentials, roles, criteria)
            }.limit(criteria.limit, criteria.offset)
    }

    fun count(database: Database, users: Users, credentials: Credentials, roles: Roles, criteria: Criteria): Query {
        return database.from(users)
            .innerJoin(credentials, on = users.id eq credentials.userId)
            .select(count()).let {
                addConditionsToQuery(it, users, credentials, roles, criteria)
            }.limit(criteria.limit, criteria.offset)
    }

    private fun addOrdersToQuery(
        query: Query,
        users: Users,
        credentials: Credentials,
        roles: Roles,
        criteria: Criteria
    ): Query {
        if (!criteria.hasOrders())
            return query

        return query.orderBy(criteria.orders.orders.mapNotNull {
            parseOrder(users, credentials, roles, it)
        })
    }

    private fun parseOrder(
        users: Users,
        credentials: Credentials,
        roles: Roles,
        order: Order
    ): OrderByExpression? {
        val orderBy = order.orderBy.value
        val orderType = order.orderType

        return when (orderBy) {
            "id" -> parseOrderType(orderType, users.id)
            "firstname" -> parseOrderType(orderType, users.firstname)
            "lastname" -> parseOrderType(orderType, users.lastname)
            "email" -> parseOrderType(orderType, credentials.email)
            "username" -> parseOrderType(orderType, credentials.username)
            "createdAt" -> parseOrderType(orderType, users.createdAt)
            "updatedAt" -> parseOrderType(orderType, users.updatedAt)
            "role.id" -> parseOrderType(orderType, roles.id)
            "role.name" -> parseOrderType(orderType, roles.name)
            else -> throw InvalidArgumentError()
        }
    }

    private fun parseOrderType(orderType: OrderType, column: Column<*>): OrderByExpression? {
        return when (orderType) {
            OrderType.ASC -> column.asc()
            OrderType.DESC -> column.desc()
            OrderType.NONE -> null
        }
    }

    private fun addConditionsToQuery(
        query: Query,
        users: Users,
        credentials: Credentials,
        roles: Roles,
        criteria: Criteria
    ): Query {
        criteria.filters.let {
            return when (it) {
                is EmptyFilters -> query
                is SingleFilter -> parseFilter(users, credentials, roles, it.filter)?.let { conditions ->
                    query.where(conditions)
                } ?: query

                is MultipleFilters -> parseMultipleFilters(users, credentials, roles, it)?.let { conditions ->
                    query.where(conditions)
                } ?: query
            }
        }
    }

    private fun parseMultipleFilters(
        users: Users,
        credentials: Credentials,
        roles: Roles,
        filters: MultipleFilters
    ): ColumnDeclaring<Boolean>? {
        if (filters.isEmpty())
            return null

        val filterConditions = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> parseFilter(users, credentials, roles, it.filter)
                is MultipleFilters -> parseMultipleFilters(users, credentials, roles, it)
                else -> null
            }
        }

        return when (filters.operator) {
            FiltersOperator.AND -> filterConditions.reduce { acc, columnDeclaring -> acc and columnDeclaring }

            FiltersOperator.OR -> filterConditions.reduce { acc, columnDeclaring -> acc or columnDeclaring }
        }
    }

    private fun parseFilter(
        users: Users,
        credentials: Credentials,
        roles: Roles,
        filter: Filter
    ): ColumnDeclaring<Boolean>? {
        val field = filter.field.value
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            "id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> users.id eq value
                    FilterOperator.NOT_EQUAL -> users.id notEq value
                    else -> null
                }
            }

            "firstname" -> {
                when (operator) {
                    FilterOperator.EQUAL -> users.firstname eq value
                    FilterOperator.NOT_EQUAL -> users.firstname notEq value
                    FilterOperator.CONTAINS -> users.firstname like "%$value%"
                    FilterOperator.NOT_CONTAINS -> users.firstname notLike "%$value%"
                    else -> null
                }
            }

            "lastname" -> {
                when (operator) {
                    FilterOperator.EQUAL -> users.lastname eq value
                    FilterOperator.NOT_EQUAL -> users.lastname notEq value
                    FilterOperator.CONTAINS -> users.lastname like "%$value%"
                    FilterOperator.NOT_CONTAINS -> users.lastname notLike "%$value%"
                    else -> null
                }
            }

            "email" -> {
                when (operator) {
                    FilterOperator.EQUAL -> credentials.email eq value
                    FilterOperator.NOT_EQUAL -> credentials.email notEq value
                    FilterOperator.CONTAINS -> credentials.email like "%$value%"
                    FilterOperator.NOT_CONTAINS -> credentials.email notLike "%$value%"
                    else -> null
                }
            }

            "username" -> {
                when (operator) {
                    FilterOperator.EQUAL -> credentials.username eq value
                    FilterOperator.NOT_EQUAL -> credentials.username notEq value
                    FilterOperator.CONTAINS -> credentials.username like "%$value%"
                    FilterOperator.NOT_CONTAINS -> credentials.username notLike "%$value%"
                    else -> null
                }
            }

            "createdAt" -> {
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

            "updatedAt" -> {
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

            "role.id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> roles.id eq value
                    FilterOperator.NOT_EQUAL -> roles.id notEq value
                    else -> null
                }
            }

            "role.name" -> {
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
