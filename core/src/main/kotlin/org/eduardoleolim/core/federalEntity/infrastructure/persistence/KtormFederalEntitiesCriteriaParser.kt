package org.eduardoleolim.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.core.federalEntity.domain.FederalEntityId
import org.eduardoleolim.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.shared.domain.criteria.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDate

object KtormFederalEntitiesCriteriaParser {
    fun select(database: Database, criteria: Criteria): Query {
        return database.from(FederalEntities).select().let {
            addOrdersToQuery(it, criteria)
        }.let {
            addConditionsToQuery(it, criteria)
        }.limit(criteria.offset, criteria.limit)
    }

    fun count(database: Database, criteria: Criteria): Query {
        return database.from(FederalEntities).select(count()).let {
            addConditionsToQuery(it, criteria)
        }.limit(criteria.offset, criteria.limit)
    }

    private fun addOrdersToQuery(
        query: Query,
        criteria: Criteria
    ): Query {
        if (!criteria.hasOrders())
            return query

        return query.orderBy(criteria.orders.orders.mapNotNull(::parseOrder))
    }

    private fun parseOrder(order: Order): OrderByExpression? {
        val orderBy = order.orderBy.value
        val orderType = order.orderType

        return when (orderBy) {
            "id" -> {
                when (orderType) {
                    OrderType.ASC -> FederalEntities.id.asc()
                    OrderType.DESC -> FederalEntities.id.desc()
                    OrderType.NONE -> null
                }
            }

            "keyCode" -> {
                when (orderType) {
                    OrderType.ASC -> FederalEntities.keyCode.asc()
                    OrderType.DESC -> FederalEntities.keyCode.desc()
                    OrderType.NONE -> null
                }
            }

            "name" -> {
                when (orderType) {
                    OrderType.ASC -> FederalEntities.name.asc()
                    OrderType.DESC -> FederalEntities.name.desc()
                    OrderType.NONE -> null
                }
            }

            "createdAt" -> {
                when (orderType) {
                    OrderType.ASC -> FederalEntities.createdAt.asc()
                    OrderType.DESC -> FederalEntities.createdAt.desc()
                    OrderType.NONE -> null
                }
            }

            "updatedAt" -> {
                when (orderType) {
                    OrderType.ASC -> FederalEntities.updatedAt.asc()
                    OrderType.DESC -> FederalEntities.updatedAt.desc()
                    OrderType.NONE -> null
                }
            }

            else -> null
        }
    }

    private fun addConditionsToQuery(
        query: Query,
        criteria: Criteria
    ): Query {
        if (criteria.hasAndFilters() && criteria.hasOrFilters()) {
            return query.where {
                val andConditions = criteria.andFilters.filters.mapNotNull(::parseFilter)
                val orConditions = criteria.orFilters.filters.mapNotNull(::parseFilter)

                if (criteria.isOrCriteria) {
                    orConditions.reduce { a, b -> a or b } or andConditions.reduce { a, b -> a and b }
                } else {
                    orConditions.reduce { a, b -> a or b } and andConditions.reduce { a, b -> a and b }
                }
            }
        } else if (criteria.hasAndFilters()) {
            return query.whereWithConditions {
                it.addAll(criteria.andFilters.filters.mapNotNull(::parseFilter))
            }
        } else {
            return query.whereWithConditions {
                it.addAll(criteria.orFilters.filters.mapNotNull(::parseFilter))
            }
        }
    }

    private fun parseFilter(filter: Filter): ColumnDeclaring<Boolean>? {
        val field = filter.field.value
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            "id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> FederalEntities.id eq FederalEntityId.fromString(value).value
                    FilterOperator.NOT_EQUAL -> FederalEntities.id notEq FederalEntityId.fromString(value).value
                    FilterOperator.GT -> FederalEntities.id greater FederalEntityId.fromString(value).value
                    FilterOperator.GTE -> FederalEntities.id greaterEq FederalEntityId.fromString(value).value
                    FilterOperator.LT -> FederalEntities.id less FederalEntityId.fromString(value).value
                    FilterOperator.LTE -> FederalEntities.id lessEq FederalEntityId.fromString(value).value
                    FilterOperator.CONTAINS -> FederalEntities.id like "%${FederalEntityId.fromString(value).value}%"
                    FilterOperator.NOT_CONTAINS -> FederalEntities.id notLike "%${FederalEntityId.fromString(value).value}%"
                }
            }

            "keyCode" -> {
                when (operator) {
                    FilterOperator.EQUAL -> FederalEntities.keyCode eq value
                    FilterOperator.NOT_EQUAL -> FederalEntities.keyCode notEq value
                    FilterOperator.GT -> FederalEntities.keyCode greater value
                    FilterOperator.GTE -> FederalEntities.keyCode greaterEq value
                    FilterOperator.LT -> FederalEntities.keyCode less value
                    FilterOperator.LTE -> FederalEntities.keyCode lessEq value
                    FilterOperator.CONTAINS -> FederalEntities.keyCode like "%$value%"
                    FilterOperator.NOT_CONTAINS -> FederalEntities.keyCode notLike "%$value%"
                }
            }

            "name" -> {
                when (operator) {
                    FilterOperator.EQUAL -> FederalEntities.name eq value
                    FilterOperator.NOT_EQUAL -> FederalEntities.name notEq value
                    FilterOperator.GT -> FederalEntities.name greater value
                    FilterOperator.GTE -> FederalEntities.name greaterEq value
                    FilterOperator.LT -> FederalEntities.name less value
                    FilterOperator.LTE -> FederalEntities.name lessEq value
                    FilterOperator.CONTAINS -> FederalEntities.name like "%$value%"
                    FilterOperator.NOT_CONTAINS -> FederalEntities.name notLike "%$value%"
                }
            }

            "createdAt" -> {
                val date = LocalDate.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> FederalEntities.createdAt eq date
                    FilterOperator.NOT_EQUAL -> FederalEntities.createdAt notEq date
                    FilterOperator.GT -> FederalEntities.createdAt greater date
                    FilterOperator.GTE -> FederalEntities.createdAt greaterEq date
                    FilterOperator.LT -> FederalEntities.createdAt less date
                    FilterOperator.LTE -> FederalEntities.createdAt lessEq date
                    else -> null
                }
            }

            "updatedAt" -> {
                val date = LocalDate.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> FederalEntities.updatedAt eq date
                    FilterOperator.NOT_EQUAL -> FederalEntities.updatedAt notEq date
                    FilterOperator.GT -> FederalEntities.updatedAt greater date
                    FilterOperator.GTE -> FederalEntities.updatedAt greaterEq date
                    FilterOperator.LT -> FederalEntities.updatedAt less date
                    FilterOperator.LTE -> FederalEntities.updatedAt lessEq date
                    else -> null
                }
            }

            else -> null
        }
    }
}
