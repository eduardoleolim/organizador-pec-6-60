package org.eduardoleolim.core.municipality.infrastructure.persistence

import org.eduardoleolim.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.core.shared.infrastructure.models.Municipalities
import org.eduardoleolim.shared.domain.criteria.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

object KtormMunicipalitiesCriteriaParser {
    fun select(
        database: Database,
        municipalities: Municipalities,
        federalEntities: FederalEntities,
        criteria: Criteria
    ): Query {
        return database.from(municipalities)
            .innerJoin(federalEntities, on = municipalities.federalEntityId eq federalEntities.id)
            .select().let {
                addOrdersToQuery(it, municipalities, federalEntities, criteria)
            }.let {
                addConditionsToQuery(it, municipalities, federalEntities, criteria)
            }.limit(criteria.offset, criteria.limit)
    }

    fun count(
        database: Database,
        municipalities: Municipalities,
        federalEntities: FederalEntities,
        criteria: Criteria
    ): Query {
        return database.from(federalEntities)
            .innerJoin(federalEntities, on = municipalities.federalEntityId eq federalEntities.id)
            .select(count()).let {
                addConditionsToQuery(it, municipalities, federalEntities, criteria)
            }.limit(criteria.offset, criteria.limit)
    }

    private fun addOrdersToQuery(
        query: Query,
        municipalities: Municipalities,
        federalEntities: FederalEntities,
        criteria: Criteria
    ): Query {
        if (!criteria.hasOrders())
            return query

        return query.orderBy(criteria.orders.orders.mapNotNull {
            parseOrder(municipalities, federalEntities, it)
        })
    }

    private fun parseOrder(
        municipalities: Municipalities,
        federalEntities: FederalEntities,
        order: Order
    ): OrderByExpression? {
        val orderBy = order.orderBy.value
        val orderType = order.orderType

        return when (orderBy) {
            "id" -> {
                when (orderType) {
                    OrderType.ASC -> municipalities.id.asc()
                    OrderType.DESC -> municipalities.id.desc()
                    OrderType.NONE -> null
                }
            }

            "keyCode" -> {
                when (orderType) {
                    OrderType.ASC -> municipalities.keyCode.asc()
                    OrderType.DESC -> municipalities.keyCode.desc()
                    OrderType.NONE -> null
                }
            }

            "name" -> {
                when (orderType) {
                    OrderType.ASC -> municipalities.name.asc()
                    OrderType.DESC -> municipalities.name.desc()
                    OrderType.NONE -> null
                }
            }

            "createdAt" -> {
                when (orderType) {
                    OrderType.ASC -> municipalities.createdAt.asc()
                    OrderType.DESC -> municipalities.createdAt.desc()
                    OrderType.NONE -> null
                }
            }

            "updatedAt" -> {
                when (orderType) {
                    OrderType.ASC -> municipalities.updatedAt.asc()
                    OrderType.DESC -> municipalities.updatedAt.desc()
                    OrderType.NONE -> null
                }
            }

            "federalEntity.id" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.id.asc()
                    OrderType.DESC -> federalEntities.id.desc()
                    OrderType.NONE -> null
                }
            }

            "federalEntity.keyCode" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.keyCode.asc()
                    OrderType.DESC -> federalEntities.keyCode.desc()
                    OrderType.NONE -> null
                }
            }

            "federalEntity.name" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.name.asc()
                    OrderType.DESC -> federalEntities.name.desc()
                    OrderType.NONE -> null
                }
            }

            "federalEntity.createdAt" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.createdAt.asc()
                    OrderType.DESC -> federalEntities.createdAt.desc()
                    OrderType.NONE -> null
                }
            }

            "federalEntity.updatedAt" -> {
                when (orderType) {
                    OrderType.ASC -> federalEntities.updatedAt.asc()
                    OrderType.DESC -> federalEntities.updatedAt.desc()
                    OrderType.NONE -> null
                }
            }

            else -> null
        }
    }

    private fun addConditionsToQuery(
        query: Query,
        municipalities: Municipalities,
        federalEntities: FederalEntities,
        criteria: Criteria
    ): Query {
        if (criteria.hasAndFilters() && criteria.hasOrFilters()) {
            return query.where {
                val andConditions = criteria.andFilters.filters.mapNotNull {
                    parseFilter(municipalities, federalEntities, it)
                }
                val orConditions = criteria.orFilters.filters.mapNotNull {
                    parseFilter(municipalities, federalEntities, it)
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
                    parseFilter(municipalities, federalEntities, filter)
                })
            }
        } else {
            return query.whereWithConditions {
                it.addAll(criteria.orFilters.filters.mapNotNull { filter ->
                    parseFilter(municipalities, federalEntities, filter)
                })
            }
        }
    }

    private fun parseFilter(
        municipalities: Municipalities,
        federalEntities: FederalEntities,
        filter: Filter
    ): ColumnDeclaring<Boolean>? {
        val field = filter.field.value
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            "id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> municipalities.id eq value
                    FilterOperator.NOT_EQUAL -> municipalities.id notEq value
                    else -> null
                }
            }

            "keyCode" -> {
                when (operator) {
                    FilterOperator.EQUAL -> municipalities.keyCode eq value
                    FilterOperator.NOT_EQUAL -> municipalities.keyCode notEq value
                    FilterOperator.GT -> municipalities.keyCode greater value
                    FilterOperator.GTE -> municipalities.keyCode greaterEq value
                    FilterOperator.LT -> municipalities.keyCode less value
                    FilterOperator.LTE -> municipalities.keyCode lessEq value
                    FilterOperator.CONTAINS -> municipalities.keyCode like "%$value%"
                    FilterOperator.NOT_CONTAINS -> municipalities.keyCode notLike "%$value%"
                }
            }

            "name" -> {
                when (operator) {
                    FilterOperator.EQUAL -> municipalities.name eq value
                    FilterOperator.NOT_EQUAL -> municipalities.name notEq value
                    FilterOperator.GT -> municipalities.name greater value
                    FilterOperator.GTE -> municipalities.name greaterEq value
                    FilterOperator.LT -> municipalities.name less value
                    FilterOperator.LTE -> municipalities.name lessEq value
                    FilterOperator.CONTAINS -> municipalities.name like "%$value%"
                    FilterOperator.NOT_CONTAINS -> municipalities.name notLike "%$value%"
                }
            }

            "createdAt" -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> municipalities.createdAt eq date
                    FilterOperator.NOT_EQUAL -> municipalities.createdAt notEq date
                    FilterOperator.GT -> municipalities.createdAt greater date
                    FilterOperator.GTE -> municipalities.createdAt greaterEq date
                    FilterOperator.LT -> municipalities.createdAt less date
                    FilterOperator.LTE -> municipalities.createdAt lessEq date
                    else -> null
                }
            }

            "updatedAt" -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> municipalities.updatedAt eq date
                    FilterOperator.NOT_EQUAL -> municipalities.updatedAt notEq date
                    FilterOperator.GT -> municipalities.updatedAt greater date
                    FilterOperator.GTE -> municipalities.updatedAt greaterEq date
                    FilterOperator.LT -> municipalities.updatedAt less date
                    FilterOperator.LTE -> municipalities.updatedAt lessEq date
                    else -> null
                }
            }

            "federalEntity.id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> federalEntities.id eq value
                    FilterOperator.NOT_EQUAL -> federalEntities.id notEq value
                    else -> null
                }
            }

            "federalEntity.keyCode" -> {
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

            "federalEntity.name" -> {
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

            "federalEntity.createdAt" -> {
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

            "federalEntity.updatedAt" -> {
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
