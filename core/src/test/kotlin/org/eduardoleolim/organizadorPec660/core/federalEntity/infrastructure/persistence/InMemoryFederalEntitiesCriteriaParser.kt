package org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorPec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.*
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
        val field = filter.field.value
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            "id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> value == record.id().toString()
                    FilterOperator.NOT_EQUAL -> value != record.id().toString()
                    else -> null
                }
            }

            "keyCode" -> {
                when (operator) {
                    FilterOperator.EQUAL -> value == record.keyCode()
                    FilterOperator.NOT_EQUAL -> value != record.keyCode()
                    FilterOperator.CONTAINS -> value.contains(record.keyCode())
                    FilterOperator.NOT_CONTAINS -> !value.contains(record.keyCode())
                    else -> null
                }
            }

            "name" -> {
                when (operator) {
                    FilterOperator.EQUAL -> value == record.name()
                    FilterOperator.NOT_EQUAL -> value != record.name()
                    FilterOperator.CONTAINS -> value.contains(record.name())
                    FilterOperator.NOT_CONTAINS -> !value.contains(record.name())
                    else -> null
                }
            }

            "createdAt" -> {
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

            "updatedAt" -> {
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

            list.sortedWith(compareBy {
                when (orderBy) {
                    "id" -> it.id()
                    "keyCode" -> it.keyCode()
                    "name" -> it.name()
                    "createdAt" -> it.createdAt()
                    "updatedAt" -> it.updatedAt()
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