/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
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

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityFields
import org.eduardoleolim.organizadorpec660.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*
import java.time.Instant
import java.util.*

object InMemoryFederalEntitiesCriteriaParser {
    fun applyFilters(records: List<FederalEntity>, criteria: Criteria): List<FederalEntity> {
        return records.filter { record ->
            criteria.filters.let {
                when (it) {
                    is EmptyFilters -> true
                    is SingleFilter -> filterPassed(record, it.filter) ?: true
                    is MultipleFilters -> filtersPassed(record, it) ?: true
                }
            }
        }
    }

    private fun filtersPassed(record: FederalEntity, filters: MultipleFilters): Boolean? {
        if (filters.isEmpty())
            return null

        val conditionResults = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> filterPassed(record, it.filter)
                is MultipleFilters -> filtersPassed(record, it)
                else -> null
            }
        }

        if (conditionResults.isEmpty())
            return null

        return when (filters.operator) {
            FiltersOperator.AND -> conditionResults.all { it }
            FiltersOperator.OR -> conditionResults.any { it }
            else -> null
        }
    }

    private fun filterPassed(record: FederalEntity, filter: Filter): Boolean? {
        val field = FederalEntityFields.entries.firstOrNull { it.value == filter.field.value }
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            FederalEntityFields.Id -> {
                when (operator) {
                    FilterOperator.EQUAL -> value == record.id().toString()
                    FilterOperator.NOT_EQUAL -> value != record.id().toString()
                    else -> null
                }
            }

            FederalEntityFields.KeyCode -> {
                when (operator) {
                    FilterOperator.EQUAL -> value == record.keyCode()
                    FilterOperator.NOT_EQUAL -> value != record.keyCode()
                    FilterOperator.CONTAINS -> value.contains(record.keyCode())
                    FilterOperator.NOT_CONTAINS -> !value.contains(record.keyCode())
                    else -> null
                }
            }

            FederalEntityFields.Name -> {
                when (operator) {
                    FilterOperator.EQUAL -> value == record.name()
                    FilterOperator.NOT_EQUAL -> value != record.name()
                    FilterOperator.CONTAINS -> value.contains(record.name())
                    FilterOperator.NOT_CONTAINS -> !value.contains(record.name())
                    else -> null
                }
            }

            FederalEntityFields.CreatedAt -> {
                val time = Date.from(Instant.parse(value)).time
                val recordTime = record.createdAt().time

                when (operator) {
                    FilterOperator.EQUAL -> time == recordTime
                    FilterOperator.NOT_EQUAL -> time != recordTime
                    FilterOperator.GT -> time > recordTime
                    FilterOperator.GTE -> time >= recordTime
                    FilterOperator.LT -> time < recordTime
                    FilterOperator.LTE -> time <= recordTime
                    else -> null
                }
            }

            FederalEntityFields.UpdatedAt -> {
                val time = Date.from(Instant.parse(value)).time
                val recordTime = record.updatedAt()?.time ?: return null

                when (operator) {
                    FilterOperator.EQUAL -> time == recordTime
                    FilterOperator.NOT_EQUAL -> time != recordTime
                    FilterOperator.GT -> time > recordTime
                    FilterOperator.GTE -> time >= recordTime
                    FilterOperator.LT -> time < recordTime
                    FilterOperator.LTE -> time <= recordTime
                    else -> null
                }
            }

            else -> throw InvalidArgumentError()
        }
    }

    fun applyOrders(records: List<FederalEntity>, criteria: Criteria): List<FederalEntity> {
        return criteria.orders.orders.fold(records) { list, order ->
            val orderBy = order.orderBy.value
            val orderType = order.orderType
            val field = FederalEntityFields.entries.firstOrNull { it.value == orderBy }

            list.sortedWith(compareBy {
                when (field) {
                    FederalEntityFields.Id -> it.id()
                    FederalEntityFields.KeyCode -> it.keyCode()
                    FederalEntityFields.Name -> it.name()
                    FederalEntityFields.CreatedAt -> it.createdAt()
                    FederalEntityFields.UpdatedAt -> it.updatedAt()
                    else -> null
                }
            }).let { if (orderType == OrderType.DESC) it.reversed() else it }
        }
    }

    fun applyPagination(records: List<FederalEntity>, limit: Int?, offset: Int?): List<FederalEntity> {
        if (limit == null || offset == null)
            return records

        return records.subList(offset, offset + limit)
    }
}
