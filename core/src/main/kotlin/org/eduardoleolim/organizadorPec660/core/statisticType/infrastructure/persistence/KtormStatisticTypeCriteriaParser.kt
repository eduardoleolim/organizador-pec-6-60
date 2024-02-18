package org.eduardoleolim.organizadorPec660.core.statisticType.infrastructure.persistence

import org.eduardoleolim.organizadorPec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.InstrumentTypes
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.InstrumentTypesOfStatisticTypes
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.StatisticTypes
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

object KtormStatisticTypeCriteriaParser {
    fun select(
        database: Database,
        statisticTypes: StatisticTypes,
        instrumentTypes: InstrumentTypes,
        instrumentTypesOfStatisticTypes: InstrumentTypesOfStatisticTypes,
        criteria: Criteria
    ): Query {
        return database.from(statisticTypes)
            .leftJoin(
                instrumentTypesOfStatisticTypes,
                on = statisticTypes.id eq instrumentTypesOfStatisticTypes.statisticTypeId
            )
            .leftJoin(instrumentTypes, on = instrumentTypes.id eq instrumentTypesOfStatisticTypes.instrumentTypeId)
            .select(statisticTypes.columns).let {
                addOrdersToQuery(it, statisticTypes, instrumentTypes, criteria)
            }.let {
                addConditionsToQuery(it, statisticTypes, instrumentTypes, criteria)
            }.limit(criteria.limit, criteria.offset)
    }

    fun count(
        database: Database,
        statisticTypes: StatisticTypes,
        instrumentTypes: InstrumentTypes,
        instrumentTypesOfStatisticTypes: InstrumentTypesOfStatisticTypes,
        criteria: Criteria
    ): Query {
        return database.from(statisticTypes)
            .leftJoin(
                instrumentTypesOfStatisticTypes,
                on = statisticTypes.id eq instrumentTypesOfStatisticTypes.statisticTypeId
            )
            .leftJoin(instrumentTypes, on = instrumentTypes.id eq instrumentTypesOfStatisticTypes.instrumentTypeId)
            .select(count()).let {
                addConditionsToQuery(it, statisticTypes, instrumentTypes, criteria)
            }.limit(criteria.limit, criteria.offset)
    }

    private fun addOrdersToQuery(
        query: Query,
        statisticTypes: StatisticTypes,
        instrumentTypes: InstrumentTypes,
        criteria: Criteria
    ): Query {
        if (!criteria.hasOrders())
            return query

        return query.orderBy(criteria.orders.orders.mapNotNull {
            parseOrder(statisticTypes, instrumentTypes, it)
        })
    }

    private fun parseOrder(
        statisticTypes: StatisticTypes,
        instrumentTypes: InstrumentTypes,
        order: Order
    ): OrderByExpression? {
        val orderBy = order.orderBy.value
        val orderType = order.orderType

        return when (orderBy) {
            "id" -> parseOrderType(orderType, statisticTypes.id)
            "keyCode" -> parseOrderType(orderType, statisticTypes.keyCode)
            "name" -> parseOrderType(orderType, statisticTypes.name)
            "createdAt" -> parseOrderType(orderType, statisticTypes.createdAt)
            "updatedAt" -> parseOrderType(orderType, statisticTypes.updatedAt)
            "instrumentType.id" -> parseOrderType(orderType, instrumentTypes.id)
            "instrumentType.name" -> parseOrderType(orderType, instrumentTypes.name)
            "instrumentType.createdAt" -> parseOrderType(orderType, instrumentTypes.createdAt)
            "instrumentType.updatedAt" -> parseOrderType(orderType, instrumentTypes.updatedAt)
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
        statisticTypes: StatisticTypes,
        instrumentTypes: InstrumentTypes,
        criteria: Criteria
    ): Query {
        criteria.filters.let {
            return when (it) {
                is EmptyFilters -> query
                is SingleFilter -> parseFilter(statisticTypes, instrumentTypes, it.filter)?.let { conditions ->
                    query.where(conditions)
                } ?: query

                is MultipleFilters -> parseMultipleFilters(statisticTypes, instrumentTypes, it)?.let { conditions ->
                    query.where(conditions)
                } ?: query
            }
        }
    }

    private fun parseMultipleFilters(
        statisticTypes: StatisticTypes,
        instrumentTypes: InstrumentTypes,
        filters: MultipleFilters
    ): ColumnDeclaring<Boolean>? {
        if (filters.isEmpty())
            return null

        val filterConditions = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> parseFilter(statisticTypes, instrumentTypes, it.filter)
                is MultipleFilters -> parseMultipleFilters(statisticTypes, instrumentTypes, it)
                else -> null
            }
        }

        return when (filters.operator) {
            FiltersOperator.AND -> filterConditions.reduceOrNull { leftCondition, rightCondition -> leftCondition and rightCondition }
            FiltersOperator.OR -> filterConditions.reduceOrNull { leftCondition, rightCondition -> leftCondition or rightCondition }
        }
    }

    private fun parseFilter(
        statisticTypes: StatisticTypes,
        instrumentTypes: InstrumentTypes,
        filter: Filter
    ): ColumnDeclaring<Boolean>? {
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

            "instrumentType.id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> instrumentTypes.id eq value
                    FilterOperator.NOT_EQUAL -> instrumentTypes.id notEq value
                    else -> null
                }
            }

            "instrumentType.name" -> {
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

            "instrumentType.createdAt" -> {
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

            "instrumentType.updatedAt" -> {
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
