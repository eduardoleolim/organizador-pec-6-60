package org.eduardoleolim.organizadorPec660.core.municipality.infrastructure.persistence

import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityFields
import org.eduardoleolim.organizadorPec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.Municipalities
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

object KtormMunicipalitiesCriteriaParser {
    fun parse(
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
        val field = MunicipalityFields.entries.firstOrNull { it.value == orderBy }

        return when (field) {
            MunicipalityFields.Id -> parseOrderType(orderType, municipalities.id)
            MunicipalityFields.KeyCode -> parseOrderType(orderType, municipalities.keyCode)
            MunicipalityFields.Name -> parseOrderType(orderType, municipalities.name)
            MunicipalityFields.CreatedAt -> parseOrderType(orderType, municipalities.createdAt)
            MunicipalityFields.UpdatedAt -> parseOrderType(orderType, municipalities.updatedAt)
            MunicipalityFields.FederalEntityId -> parseOrderType(orderType, federalEntities.id)
            MunicipalityFields.FederalEntityKeyCode -> parseOrderType(orderType, federalEntities.keyCode)
            MunicipalityFields.FederalEntityName -> parseOrderType(orderType, federalEntities.name)
            MunicipalityFields.FederalEntityCreatedAt -> parseOrderType(orderType, federalEntities.createdAt)
            MunicipalityFields.FederalEntityUpdatedAt -> parseOrderType(orderType, federalEntities.updatedAt)
            null -> throw InvalidArgumentError()
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
        municipalities: Municipalities,
        federalEntities: FederalEntities,
        criteria: Criteria
    ): Query {
        criteria.filters.let {
            return when (it) {
                is EmptyFilters -> query
                is SingleFilter -> parseFilter(municipalities, federalEntities, it.filter)?.let { conditions ->
                    query.where(conditions)
                } ?: query

                is MultipleFilters -> parseMultipleFilters(municipalities, federalEntities, it)?.let { conditions ->
                    query.where(conditions)
                } ?: query
            }
        }
    }

    private fun parseMultipleFilters(
        municipalities: Municipalities,
        federalEntities: FederalEntities,
        filters: MultipleFilters
    ): ColumnDeclaring<Boolean>? {
        if (filters.isEmpty())
            return null

        val filterConditions = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> parseFilter(municipalities, federalEntities, it.filter)
                is MultipleFilters -> parseMultipleFilters(municipalities, federalEntities, it)
                else -> null
            }
        }

        return when (filters.operator) {
            FiltersOperator.AND -> filterConditions.reduceOrNull { leftCondition, rightCondition -> leftCondition and rightCondition }
            FiltersOperator.OR -> filterConditions.reduceOrNull { leftCondition, rightCondition -> leftCondition or rightCondition }
        }
    }

    private fun parseFilter(
        municipalities: Municipalities,
        federalEntities: FederalEntities,
        filter: Filter
    ): ColumnDeclaring<Boolean>? {
        val field = MunicipalityFields.entries.firstOrNull { it.value == filter.field.value }
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            MunicipalityFields.Id -> {
                when (operator) {
                    FilterOperator.EQUAL -> municipalities.id eq value
                    FilterOperator.NOT_EQUAL -> municipalities.id notEq value
                    else -> null
                }
            }

            MunicipalityFields.KeyCode -> {
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

            MunicipalityFields.Name -> {
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

            MunicipalityFields.CreatedAt -> {
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

            MunicipalityFields.UpdatedAt -> {
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

            MunicipalityFields.FederalEntityId -> {
                when (operator) {
                    FilterOperator.EQUAL -> federalEntities.id eq value
                    FilterOperator.NOT_EQUAL -> federalEntities.id notEq value
                    else -> null
                }
            }

            MunicipalityFields.FederalEntityKeyCode -> {
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

            MunicipalityFields.FederalEntityName -> {
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

            MunicipalityFields.FederalEntityCreatedAt -> {
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

            MunicipalityFields.FederalEntityUpdatedAt -> {
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

            null -> throw InvalidArgumentError()
        }
    }
}
