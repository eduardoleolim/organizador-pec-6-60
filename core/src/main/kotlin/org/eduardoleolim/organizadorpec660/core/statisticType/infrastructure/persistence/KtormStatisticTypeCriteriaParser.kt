package org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.StatisticTypes
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeFields
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

class KtormStatisticTypeCriteriaParser(private val database: Database, private val statisticTypes: StatisticTypes) {
    fun selectQuery(criteria: Criteria): Query {
        return database.from(statisticTypes)
            .selectDistinct(statisticTypes.columns).let {
                addOrdersToQuery(it, criteria)
            }.let {
                addConditionsToQuery(it, criteria)
            }.limit(criteria.offset, criteria.limit)
    }

    fun countQuery(criteria: Criteria): Query {
        val baseQuery = selectQuery(criteria)
        val querySource = QuerySource(database, statisticTypes, baseQuery.expression)

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
        val field = StatisticTypeFields.entries.firstOrNull { it.value == orderBy }
        val column = when (field) {
            StatisticTypeFields.Id -> statisticTypes.id
            StatisticTypeFields.KeyCode -> statisticTypes.keyCode
            StatisticTypeFields.Name -> statisticTypes.name
            StatisticTypeFields.CreatedAt -> statisticTypes.createdAt
            StatisticTypeFields.UpdatedAt -> statisticTypes.updatedAt
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
            FiltersOperator.AND -> filterConditions.reduceOrNull { left, right -> left and right }
            FiltersOperator.OR -> filterConditions.reduceOrNull { left, right -> left or right }
        }
    }

    private fun parseFilter(filter: Filter): ColumnDeclaring<Boolean>? {
        val field = StatisticTypeFields.entries.firstOrNull { it.value == filter.field.value }
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            StatisticTypeFields.Id -> {
                when (operator) {
                    FilterOperator.EQUAL -> statisticTypes.id eq value
                    FilterOperator.NOT_EQUAL -> statisticTypes.id notEq value
                    else -> null
                }
            }

            StatisticTypeFields.KeyCode -> {
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

            StatisticTypeFields.Name -> {
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

            StatisticTypeFields.CreatedAt -> {
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

            StatisticTypeFields.UpdatedAt -> {
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
