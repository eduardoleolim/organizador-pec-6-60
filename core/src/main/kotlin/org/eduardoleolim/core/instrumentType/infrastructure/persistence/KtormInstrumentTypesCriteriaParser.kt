package org.eduardoleolim.core.instrumentType.infrastructure.persistence

import org.eduardoleolim.core.shared.infrastructure.models.InstrumentTypes
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

object KtormInstrumentTypesCriteriaParser {
    fun select(
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

    fun count(
        database: Database,
        instrumentTypes: InstrumentTypes,
        criteria: Criteria
    ): Query {
        return database.from(instrumentTypes)
            .select().let {
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
            "id" -> {
                when (orderType) {
                    OrderType.ASC -> instrumentTypes.id.asc()
                    OrderType.DESC -> instrumentTypes.id.desc()
                    OrderType.NONE -> null
                }
            }

            "name" -> {
                when (orderType) {
                    OrderType.ASC -> instrumentTypes.name.asc()
                    OrderType.DESC -> instrumentTypes.name.desc()
                    OrderType.NONE -> null
                }
            }

            "createdAt" -> {
                when (orderType) {
                    OrderType.ASC -> instrumentTypes.createdAt.asc()
                    OrderType.DESC -> instrumentTypes.createdAt.desc()
                    OrderType.NONE -> null
                }
            }

            "updatedAt" -> {
                when (orderType) {
                    OrderType.ASC -> instrumentTypes.updatedAt.asc()
                    OrderType.DESC -> instrumentTypes.updatedAt.desc()
                    OrderType.NONE -> null
                }
            }

            else -> null
        }
    }

    private fun addConditionsToQuery(
        query: Query,
        instrumentTypes: InstrumentTypes,
        criteria: Criteria
    ): Query {
        if (!criteria.hasAndFilters() && !criteria.hasOrFilters())
            return query

        if (criteria.hasAndFilters() && criteria.hasOrFilters()) {
            return query.where {
                val andConditions = criteria.andFilters.filters.mapNotNull {
                    parseFilter(instrumentTypes, it)
                }
                val orConditions = criteria.orFilters.filters.mapNotNull {
                    parseFilter(instrumentTypes, it)
                }

                if (criteria.isOrCriteria) {
                    orConditions.reduce { a, b -> a or b } or andConditions.reduce { a, b -> a and b }
                } else {
                    orConditions.reduce { a, b -> a or b } and andConditions.reduce { a, b -> a and b }
                }
            }
        }

        if (criteria.hasAndFilters()) {
            return query.whereWithConditions {
                it.addAll(criteria.andFilters.filters.mapNotNull { filter ->
                    parseFilter(instrumentTypes, filter)
                })
            }
        }

        return query.whereWithOrConditions {
            it.addAll(criteria.orFilters.filters.mapNotNull { filter ->
                parseFilter(instrumentTypes, filter)
            })
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

            else -> null
        }
    }
}
