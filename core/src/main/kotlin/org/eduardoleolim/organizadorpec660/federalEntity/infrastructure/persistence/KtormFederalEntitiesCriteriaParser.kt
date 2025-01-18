/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityFields
import org.eduardoleolim.organizadorpec660.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.FederalEntities
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

class KtormFederalEntitiesCriteriaParser(private val database: Database, private val federalEntities: FederalEntities) {
    fun selectQuery(criteria: Criteria): Query {
        return database.from(federalEntities).selectDistinct().let {
            addOrdersToQuery(it, criteria)
        }.let {
            addConditionsToQuery(it, criteria)
        }.limit(criteria.offset, criteria.limit)
    }

    fun countQuery(criteria: Criteria): Query {
        val baseQuery = selectQuery(criteria)
        val querySource = QuerySource(database, federalEntities, baseQuery.expression)

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
        val field = FederalEntityFields.entries.firstOrNull { it.value == orderBy }
        val column = when (field) {
            FederalEntityFields.Id -> federalEntities.id
            FederalEntityFields.KeyCode -> federalEntities.keyCode
            FederalEntityFields.Name -> federalEntities.name
            FederalEntityFields.CreatedAt -> federalEntities.createdAt
            FederalEntityFields.UpdatedAt -> federalEntities.updatedAt
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

        val conditions = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> parseFilter(it.filter)
                is MultipleFilters -> parseMultipleFilters(it)
                else -> null
            }
        }

        return when (filters.operator) {
            FiltersOperator.AND -> conditions.reduceOrNull { left, right -> left and right }
            FiltersOperator.OR -> conditions.reduceOrNull { left, right -> left or right }
        }
    }

    private fun parseFilter(filter: Filter): ColumnDeclaring<Boolean>? {
        val field = FederalEntityFields.entries.firstOrNull { it.value == filter.field.value }
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            FederalEntityFields.Id -> {
                when (operator) {
                    FilterOperator.EQUAL -> federalEntities.id eq value
                    FilterOperator.NOT_EQUAL -> federalEntities.id notEq value
                    else -> null
                }
            }

            FederalEntityFields.KeyCode -> {
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

            FederalEntityFields.Name -> {
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

            FederalEntityFields.CreatedAt -> {
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

            FederalEntityFields.UpdatedAt -> {
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
