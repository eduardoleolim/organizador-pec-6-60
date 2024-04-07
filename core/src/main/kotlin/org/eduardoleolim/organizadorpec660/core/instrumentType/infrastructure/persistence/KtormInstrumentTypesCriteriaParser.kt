package org.eduardoleolim.organizadorpec660.core.instrumentType.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.InstrumentTypes
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

object KtormInstrumentTypesCriteriaParser {
    fun parse(
        database: Database,
        instrumentTypes: InstrumentTypes,
        criteria: Criteria
    ): Query {
        return database.from(instrumentTypes)
            .select().let {
                addOrdersToQuery(it, instrumentTypes, criteria)
            }.let {
                addConditionsToQuery(it, instrumentTypes, criteria)
            }.limit(criteria.offset, criteria.limit)
    }

    private fun addOrdersToQuery(
        query: Query,
        instrumentTypes: InstrumentTypes,
        criteria: Criteria
    ): Query {
        if (!criteria.hasOrders())
            return query

        return query.orderBy(criteria.orders.orders.mapNotNull {
            parseOrder(instrumentTypes, it)
        })
    }

    private fun parseOrder(
        instrumentTypes: InstrumentTypes,
        order: Order
    ): OrderByExpression? {
        val orderBy = order.orderBy.value
        val orderType = order.orderType

        return when (orderBy) {
            "id" -> parseOrderType(orderType, instrumentTypes.id)
            "name" -> parseOrderType(orderType, instrumentTypes.name)
            "createdAt" -> parseOrderType(orderType, instrumentTypes.createdAt)
            "updatedAt" -> parseOrderType(orderType, instrumentTypes.updatedAt)
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
        instrumentTypes: InstrumentTypes,
        criteria: Criteria
    ): Query {
        criteria.filters.let {
            return when (it) {
                is EmptyFilters -> query
                is SingleFilter -> parseFilter(instrumentTypes, it.filter)?.let { conditions ->
                    query.where(conditions)
                } ?: query

                is MultipleFilters -> parseMultipleFilters(instrumentTypes, it)?.let { conditions ->
                    query.where(conditions)
                } ?: query
            }
        }
    }

    private fun parseMultipleFilters(
        instrumentTypes: InstrumentTypes,
        filters: MultipleFilters
    ): ColumnDeclaring<Boolean>? {
        if (filters.isEmpty())
            return null

        val conditions = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> parseFilter(instrumentTypes, it.filter)
                is MultipleFilters -> parseMultipleFilters(instrumentTypes, it)
                else -> null
            }
        }

        return when (filters.operator) {
            FiltersOperator.AND -> conditions.reduce { acc, condition -> acc and condition }
            FiltersOperator.OR -> conditions.reduce { acc, condition -> acc or condition }
        }
    }

    private fun parseFilter(
        instrumentTypes: InstrumentTypes,
        filter: Filter
    ): ColumnDeclaring<Boolean>? {
        val field = filter.field.value
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            "id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> instrumentTypes.id eq value
                    FilterOperator.NOT_EQUAL -> instrumentTypes.id notEq value
                    else -> null
                }
            }

            "name" -> {
                when (operator) {
                    FilterOperator.EQUAL -> instrumentTypes.name eq value
                    FilterOperator.NOT_EQUAL -> instrumentTypes.name notEq value
                    FilterOperator.GT -> instrumentTypes.name greater value
                    FilterOperator.GTE -> instrumentTypes.name greaterEq value
                    FilterOperator.LT -> instrumentTypes.name less value
                    FilterOperator.LTE -> instrumentTypes.name lessEq value
                    FilterOperator.CONTAINS -> instrumentTypes.name like "%$value%"
                    FilterOperator.NOT_CONTAINS -> instrumentTypes.name notLike "%$value%"
                }
            }

            "createdAt" -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> instrumentTypes.createdAt eq date
                    FilterOperator.NOT_EQUAL -> instrumentTypes.createdAt notEq date
                    FilterOperator.GT -> instrumentTypes.createdAt greater date
                    FilterOperator.GTE -> instrumentTypes.createdAt greaterEq date
                    FilterOperator.LT -> instrumentTypes.createdAt less date
                    FilterOperator.LTE -> instrumentTypes.createdAt lessEq date
                    else -> null
                }
            }

            "updatedAt" -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> instrumentTypes.updatedAt eq date
                    FilterOperator.NOT_EQUAL -> instrumentTypes.updatedAt notEq date
                    FilterOperator.GT -> instrumentTypes.updatedAt greater date
                    FilterOperator.GTE -> instrumentTypes.updatedAt greaterEq date
                    FilterOperator.LT -> instrumentTypes.updatedAt less date
                    FilterOperator.LTE -> instrumentTypes.updatedAt lessEq date
                    else -> null
                }
            }

            else -> throw InvalidArgumentError()
        }
    }
}
