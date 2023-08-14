package org.eduardoleolim.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.core.federalEntity.domain.FederalEntityId
import org.eduardoleolim.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.shared.domain.criteria.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
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
            "id" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.id.asc()
                    OrderType.DESC -> federalEntities.id.desc()
                    OrderType.NONE -> null
                }
            }

            "keyCode" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.keyCode.asc()
                    OrderType.DESC -> federalEntities.keyCode.desc()
                    OrderType.NONE -> null
                }
            }

            "name" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.name.asc()
                    OrderType.DESC -> federalEntities.name.desc()
                    OrderType.NONE -> null
                }
            }

            "createdAt" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.createdAt.asc()
                    OrderType.DESC -> federalEntities.createdAt.desc()
                    OrderType.NONE -> null
                }
            }

            "updatedAt" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.updatedAt.asc()
                    OrderType.DESC -> federalEntities.updatedAt.desc()
                    OrderType.NONE -> null
                }
            }

            else -> null
        }
    }

    private fun addConditionsToQuery(query: Query, federalEntities: FederalEntities, criteria: Criteria): Query {
        if (criteria.hasAndFilters() && criteria.hasOrFilters()) {
            return query.where {
                val andConditions = criteria.andFilters.filters.mapNotNull {
                    parseFilter(federalEntities, it)
                }
                val orConditions = criteria.orFilters.filters.mapNotNull {
                    parseFilter(federalEntities, it)
                }

                if (criteria.isOrCriteria) {
                    orConditions.reduce { a, b -> a or b } or andConditions.reduce { a, b -> a and b }
                } else {
                    orConditions.reduce { a, b -> a or b } and andConditions.reduce { a, b -> a and b }
                }
            }
        } else if (criteria.hasAndFilters()) {
            return query.whereWithConditions {
                it.addAll(criteria.andFilters.filters.mapNotNull { filter ->
                    parseFilter(federalEntities, filter)
                })
            }
        } else {
            return query.whereWithConditions {
                it.addAll(criteria.orFilters.filters.mapNotNull { filter ->
                    parseFilter(federalEntities, filter)
                })
            }
        }
    }

    private fun parseFilter(federalEntities: FederalEntities, filter: Filter): ColumnDeclaring<Boolean>? {
        val field = filter.field.value
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            "id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> federalEntities.id eq FederalEntityId.fromString(value).value
                    FilterOperator.NOT_EQUAL -> federalEntities.id notEq FederalEntityId.fromString(value).value
                    FilterOperator.GT -> federalEntities.id greater FederalEntityId.fromString(value).value
                    FilterOperator.GTE -> federalEntities.id greaterEq FederalEntityId.fromString(value).value
                    FilterOperator.LT -> federalEntities.id less FederalEntityId.fromString(value).value
                    FilterOperator.LTE -> federalEntities.id lessEq FederalEntityId.fromString(value).value
                    FilterOperator.CONTAINS -> federalEntities.id like "%${FederalEntityId.fromString(value).value}%"
                    FilterOperator.NOT_CONTAINS -> federalEntities.id notLike "%${FederalEntityId.fromString(value).value}%"
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

            else -> null
        }
    }
}
