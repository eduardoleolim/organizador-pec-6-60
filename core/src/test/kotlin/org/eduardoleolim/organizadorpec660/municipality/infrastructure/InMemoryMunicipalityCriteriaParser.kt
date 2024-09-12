package org.eduardoleolim.organizadorpec660.municipality.infrastructure

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.*
import java.time.Instant
import java.util.*

object InMemoryMunicipalityCriteriaParser {
    fun applyFilters(
        municipalities: List<Municipality>,
        federalEntities: List<FederalEntity>,
        criteria: Criteria
    ): List<Municipality> {
        return municipalities.filter { municipality ->
            val federalEntity = federalEntities.find { it.id() == municipality.federalEntityId() }

            criteria.filters.let {
                when (it) {
                    is EmptyFilters -> true
                    is SingleFilter -> filterPassed(municipality, federalEntity, it.filter) ?: true
                    is MultipleFilters -> filtersPassed(municipality, federalEntity, it) ?: true
                }
            }
        }
    }

    private fun filtersPassed(record: Municipality, federalEntity: FederalEntity?, filters: MultipleFilters): Boolean? {
        if (filters.isEmpty())
            return null

        val conditionResults = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> filterPassed(record, federalEntity, it.filter)
                is MultipleFilters -> filtersPassed(record, federalEntity, it)
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

    private fun filterPassed(record: Municipality, federalEntity: FederalEntity?, filter: Filter): Boolean? {
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

            "federalEntity.id" -> {
                when (operator) {
                    FilterOperator.EQUAL -> value == record.federalEntityId().toString()
                    FilterOperator.NOT_EQUAL -> value != record.federalEntityId().toString()
                    else -> null
                }
            }

            "federalEntity.keyCode" -> {
                if (federalEntity == null)
                    return null

                when (operator) {
                    FilterOperator.EQUAL -> value == federalEntity.keyCode()
                    FilterOperator.NOT_EQUAL -> value != federalEntity.keyCode()
                    FilterOperator.CONTAINS -> value.contains(federalEntity.keyCode())
                    FilterOperator.NOT_CONTAINS -> !value.contains(federalEntity.keyCode())
                    else -> null
                }
            }

            "federalEntity.name" -> {
                if (federalEntity == null)
                    return null

                when (operator) {
                    FilterOperator.EQUAL -> value == federalEntity.name()
                    FilterOperator.NOT_EQUAL -> value != federalEntity.name()
                    FilterOperator.CONTAINS -> value.contains(federalEntity.name())
                    FilterOperator.NOT_CONTAINS -> !value.contains(federalEntity.name())
                    else -> null
                }
            }

            "federalEntity.createdAt" -> {
                if (federalEntity == null)
                    return null

                val time = Date.from(Instant.parse(value)).time
                val recordTime = federalEntity.createdAt().time

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

            "federalEntity.updatedAt" -> {
                if (federalEntity == null)
                    return null

                val time = Date.from(Instant.parse(value)).time
                val recordTime = federalEntity.updatedAt()?.time ?: return null

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

    fun applyOrders(
        municipalities: List<Municipality>,
        federalEntities: List<FederalEntity>,
        criteria: Criteria
    ): List<Municipality> {
        return criteria.orders.orders.fold(municipalities) { list, order ->
            val orderBy = order.orderBy.value
            val orderType = order.orderType

            list.sortedWith(compareBy {
                val federalEntity = federalEntities.find { item -> item.id() == it.federalEntityId() }

                when (orderBy) {
                    "id" -> it.id()
                    "keyCode" -> it.keyCode()
                    "name" -> it.name()
                    "createdAt" -> it.createdAt()
                    "updatedAt" -> it.updatedAt()
                    "federalEntity.id" -> it.federalEntityId()
                    "federalEntity.keyCode" -> federalEntity?.keyCode()
                    "federalEntity.name" -> federalEntity?.name()
                    "federalEntity.createdAt" -> federalEntity?.createdAt()
                    "federalEntity.updatedAt" -> federalEntity?.updatedAt()
                    else -> null
                }
            }).let { if (orderType == OrderType.DESC) it.reversed() else it }
        }
    }

    fun applyPagination(records: List<Municipality>, limit: Int?, offset: Int?): List<Municipality> {
        if (limit == null || offset == null)
            return records

        return records.subList(offset, offset + limit)
    }
}
