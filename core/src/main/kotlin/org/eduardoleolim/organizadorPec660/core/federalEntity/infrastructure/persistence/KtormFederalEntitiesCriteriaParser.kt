package org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.organizadorPec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.FederalEntities
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

object KtormFederalEntitiesCriteriaParser {
    fun select(database: Database, federalEntities: FederalEntities, criteria: Criteria): Query {
        return database.from(federalEntities).select().let {
            addOrdersToQuery(it, federalEntities, criteria)
        }.let {
            addConditionsToQuery(it, federalEntities, criteria)
        }.limit(criteria.offset, criteria.limit)
    }

    fun count(database: Database, federalEntities: FederalEntities, criteria: Criteria): Query {
        return database.from(federalEntities).select(count()).let {
            addConditionsToQuery(it, federalEntities, criteria)
        }.limit(criteria.offset, criteria.limit)
    }

    private fun addOrdersToQuery(query: Query, federalEntities: FederalEntities, criteria: Criteria): Query {
        if (!criteria.hasOrders())
            return query

        return query.orderBy(criteria.orders.orders.mapNotNull {
            parseOrder(federalEntities, it)
        })
    }

    private fun parseOrder(federalEntities: FederalEntities, order: Order): OrderByExpression? {
        val orderBy = order.orderBy.value
        val orderType = order.orderType

        return when (orderBy) {
            "id" -> parseOrderType(orderType, federalEntities.id)
            "keyCode" -> parseOrderType(orderType, federalEntities.keyCode)
            "name" -> parseOrderType(orderType, federalEntities.name)
            "createdAt" -> parseOrderType(orderType, federalEntities.createdAt)
            "updatedAt" -> parseOrderType(orderType, federalEntities.updatedAt)
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

    private fun addConditionsToQuery(query: Query, federalEntities: FederalEntities, criteria: Criteria): Query {
        criteria.filters.let {
            return when (it) {
                is EmptyFilters -> query
                is SingleFilter -> parseFilter(federalEntities, it.filter)?.let { conditions ->
                    query.where(conditions)
                } ?: query

                is MultipleFilters -> parseMultipleFilters(federalEntities, it)?.let { conditions ->
                    query.where(conditions)
                } ?: query
            }
        }
    }

    private fun parseMultipleFilters(
        federalEntities: FederalEntities,
        filters: MultipleFilters
    ): ColumnDeclaring<Boolean>? {
        if (filters.isEmpty())
            return null

        val filterConditions = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> parseFilter(federalEntities, it.filter)
                is MultipleFilters -> parseMultipleFilters(federalEntities, it)
                else -> null
            }
        }

        return when (filters.operator) {
            FiltersOperator.AND -> filterConditions.reduce { acc, columnDeclaring -> acc and columnDeclaring }
            FiltersOperator.OR -> filterConditions.reduce { acc, columnDeclaring -> acc or columnDeclaring }
        }
    }

    private fun parseFilter(federalEntities: FederalEntities, filter: Filter): ColumnDeclaring<Boolean>? {
        val field = filter.field.value
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            "id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> federalEntities.id eq value
                    FilterOperator.NOT_EQUAL -> federalEntities.id notEq value
                    else -> null
                }
            }

            "keyCode" -> {
                when (operator) {
                    FilterOperator.EQUAL -> federalEntities.keyCode eq value
                    FilterOperator.NOT_EQUAL -> federalEntities.keyCode notEq value
                    FilterOperator.GT -> federalEntities.keyCode greater value
                    FilterOperator.GTE -> federalEntities.keyCode greaterEq value
                    FilterOperator.LT -> federalEntities.keyCode less value
                    FilterOperator.LTE -> federalEntities.keyCode lessEq value
                    FilterOperator.CONTAINS -> federalEntities.keyCode like "%$value%"
                    FilterOperator.NOT_CONTAINS -> federalEntities.keyCode notLike "%$value%"
                }
            }

            "name" -> {
                when (operator) {
                    FilterOperator.EQUAL -> federalEntities.name eq value
                    FilterOperator.NOT_EQUAL -> federalEntities.name notEq value
                    FilterOperator.GT -> federalEntities.name greater value
                    FilterOperator.GTE -> federalEntities.name greaterEq value
                    FilterOperator.LT -> federalEntities.name less value
                    FilterOperator.LTE -> federalEntities.name lessEq value
                    FilterOperator.CONTAINS -> federalEntities.name like "%$value%"
                    FilterOperator.NOT_CONTAINS -> federalEntities.name notLike "%$value%"
                }
            }

            "createdAt" -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> federalEntities.createdAt eq date
                    FilterOperator.NOT_EQUAL -> federalEntities.createdAt notEq date
                    FilterOperator.GT -> federalEntities.createdAt greater date
                    FilterOperator.GTE -> federalEntities.createdAt greaterEq date
                    FilterOperator.LT -> federalEntities.createdAt less date
                    FilterOperator.LTE -> federalEntities.createdAt lessEq date
                    else -> null
                }
            }

            "updatedAt" -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> federalEntities.updatedAt eq date
                    FilterOperator.NOT_EQUAL -> federalEntities.updatedAt notEq date
                    FilterOperator.GT -> federalEntities.updatedAt greater date
                    FilterOperator.GTE -> federalEntities.updatedAt greaterEq date
                    FilterOperator.LT -> federalEntities.updatedAt less date
                    FilterOperator.LTE -> federalEntities.updatedAt lessEq date
                    else -> null
                }
            }

            else -> throw InvalidArgumentError()
        }
    }
}
