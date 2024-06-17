package org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.StatisticTypes
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

object KtormStatisticTypeCriteriaParser {
    fun parse(
        database: Database,
        statisticTypes: StatisticTypes,
        criteria: Criteria
    ): Query {
        return database.from(statisticTypes)
            .selectDistinct(statisticTypes.columns).let {
                addOrdersToQuery(it, statisticTypes, criteria)
            }.let {
                it.totalRecordsInAllPages
                addConditionsToQuery(it, statisticTypes, criteria)
            }.limit(criteria.offset, criteria.limit)
    }

    private fun addOrdersToQuery(query: Query, statisticTypes: StatisticTypes, criteria: Criteria): Query {
        if (!criteria.hasOrders())
            return query

        return query.orderBy(criteria.orders.orders.mapNotNull {
            parseOrder(statisticTypes, it)
        })
    }

    private fun parseOrder(statisticTypes: StatisticTypes, order: Order): OrderByExpression? {
        val orderBy = order.orderBy.value
        val orderType = order.orderType

        return when (orderBy) {
            "id" -> parseOrderType(orderType, statisticTypes.id)
            "keyCode" -> parseOrderType(orderType, statisticTypes.keyCode)
            "name" -> parseOrderType(orderType, statisticTypes.name)
            "createdAt" -> parseOrderType(orderType, statisticTypes.createdAt)
            "updatedAt" -> parseOrderType(orderType, statisticTypes.updatedAt)
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

    private fun addConditionsToQuery(query: Query, statisticTypes: StatisticTypes, criteria: Criteria): Query {
        criteria.filters.let {
            return when (it) {
                is EmptyFilters -> query
                is SingleFilter -> parseFilter(statisticTypes, it.filter)?.let { conditions ->
                    query.where(conditions)
                } ?: query

                is MultipleFilters -> parseMultipleFilters(statisticTypes, it)?.let { conditions ->
                    query.where(conditions)
                } ?: query
            }
        }
    }

    private fun parseMultipleFilters(
        statisticTypes: StatisticTypes,
        filters: MultipleFilters
    ): ColumnDeclaring<Boolean>? {
        if (filters.isEmpty())
            return null

        val filterConditions = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> parseFilter(statisticTypes, it.filter)
                is MultipleFilters -> parseMultipleFilters(statisticTypes, it)
                else -> null
            }
        }

        return when (filters.operator) {
            FiltersOperator.AND -> filterConditions.reduceOrNull { leftCondition, rightCondition -> leftCondition and rightCondition }
            FiltersOperator.OR -> filterConditions.reduceOrNull { leftCondition, rightCondition -> leftCondition or rightCondition }
        }
    }

    private fun parseFilter(statisticTypes: StatisticTypes, filter: Filter): ColumnDeclaring<Boolean>? {
        val field = filter.field.value
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            "id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> statisticTypes.id eq value
                    FilterOperator.NOT_EQUAL -> statisticTypes.id notEq value
                    else -> null
                }
            }

            "keyCode" -> {
                when (operator) {
                    FilterOperator.EQUAL -> statisticTypes.keyCode eq value
                    FilterOperator.NOT_EQUAL -> statisticTypes.keyCode notEq value
                    FilterOperator.GT -> statisticTypes.keyCode greater value
                    FilterOperator.GTE -> statisticTypes.keyCode greaterEq value
                    FilterOperator.LT -> statisticTypes.keyCode less value
                    FilterOperator.LTE -> statisticTypes.keyCode lessEq value
                    FilterOperator.CONTAINS -> statisticTypes.keyCode like "%$value%"
                    FilterOperator.NOT_CONTAINS -> statisticTypes.keyCode notLike "%$value%"
                }
            }

            "name" -> {
                when (operator) {
                    FilterOperator.EQUAL -> statisticTypes.name eq value
                    FilterOperator.NOT_EQUAL -> statisticTypes.name notEq value
                    FilterOperator.GT -> statisticTypes.name greater value
                    FilterOperator.GTE -> statisticTypes.name greaterEq value
                    FilterOperator.LT -> statisticTypes.name less value
                    FilterOperator.LTE -> statisticTypes.name lessEq value
                    FilterOperator.CONTAINS -> statisticTypes.name like "%$value%"
                    FilterOperator.NOT_CONTAINS -> statisticTypes.name notLike "%$value%"
                }
            }

            "createdAt" -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> statisticTypes.createdAt eq date
                    FilterOperator.NOT_EQUAL -> statisticTypes.createdAt notEq date
                    FilterOperator.GT -> statisticTypes.createdAt greater date
                    FilterOperator.GTE -> statisticTypes.createdAt greaterEq date
                    FilterOperator.LT -> statisticTypes.createdAt less date
                    FilterOperator.LTE -> statisticTypes.createdAt lessEq date
                    else -> null
                }
            }

            "updatedAt" -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> statisticTypes.updatedAt eq date
                    FilterOperator.NOT_EQUAL -> statisticTypes.updatedAt notEq date
                    FilterOperator.GT -> statisticTypes.updatedAt greater date
                    FilterOperator.GTE -> statisticTypes.updatedAt greaterEq date
                    FilterOperator.LT -> statisticTypes.updatedAt less date
                    FilterOperator.LTE -> statisticTypes.updatedAt lessEq date
                    else -> null
                }
            }

            else -> throw InvalidArgumentError()
        }
    }
}
