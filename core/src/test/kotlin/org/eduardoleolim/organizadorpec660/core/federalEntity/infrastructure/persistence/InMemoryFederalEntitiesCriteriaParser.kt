package org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Filter
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.FilterOperator
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.OrderType
import java.time.Instant
import java.util.*

object InMemoryFederalEntitiesCriteriaParser {
    fun applyFilters(records: List<FederalEntity>, criteria: Criteria): List<FederalEntity> {
        return records.mapNotNull { record ->
            // Apply no filters
            if (!criteria.hasOrFilters() && !criteria.hasAndFilters()) {
                return@mapNotNull record
            }

            // If there is no AND filters, then there is OR filters
            if (!criteria.hasAndFilters()) {
                criteria.orFilters.filters.mapNotNull { filter ->
                    filterPassed(record, filter)
                }.let {
                    if (it.contains(true)) {
                        return@mapNotNull record
                    } else {
                        return@mapNotNull null
                    }
                }
            }

            // Apply AND filters
            if (!criteria.hasOrFilters()) {
                criteria.andFilters.filters.mapNotNull { filter ->
                    filterPassed(record, filter)
                }.let {
                    if (it.contains(false)) {
                        return@mapNotNull null
                    } else {
                        return@mapNotNull record
                    }
                }
            }

            // Apply both kinds of filters
            val andResults = !criteria.andFilters.filters.mapNotNull { filter ->
                filterPassed(record, filter)
            }.contains(false)
            val orResults = criteria.orFilters.filters.mapNotNull { filter ->
                filterPassed(record, filter)
            }.contains(true)

            if (criteria.isOrCriteria) {
                if (orResults || andResults) {
                    return@mapNotNull record
                } else {
                    return@mapNotNull null
                }
            } else {
                if (orResults && andResults) {
                    return@mapNotNull record
                } else {
                    return@mapNotNull null
                }
            }
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

            else -> null
        }
    }

    fun applyOrders(
        records: List<FederalEntity>,
        criteria: org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
    ): List<FederalEntity> {
        if (!criteria.hasOrders())
            return records

        var orderedRecords = records

        criteria.orders.orders.forEach {
            val orderBy = it.orderBy.value
            val orderType = it.orderType

            orderedRecords = when (orderBy) {
                "id" -> {
                    when (orderType) {
                        OrderType.ASC -> orderedRecords.sortedBy { record -> record.id() }
                        OrderType.DESC -> orderedRecords.sortedByDescending { record -> record.id() }
                        else -> orderedRecords
                    }
                }

                "keyCode" -> {
                    when (orderType) {
                        OrderType.ASC -> orderedRecords.sortedBy { record -> record.keyCode() }
                        OrderType.DESC -> orderedRecords.sortedByDescending { record -> record.keyCode() }
                        else -> orderedRecords
                    }
                }

                "name" -> {
                    when (orderType) {
                        OrderType.ASC -> orderedRecords.sortedBy { record -> record.name() }
                        OrderType.DESC -> orderedRecords.sortedByDescending { record -> record.name() }
                        else -> orderedRecords
                    }
                }

                "createdAt" -> {
                    when (orderType) {
                        OrderType.ASC -> orderedRecords.sortedBy { record -> record.createdAt() }
                        OrderType.DESC -> orderedRecords.sortedByDescending { record -> record.createdAt() }
                        else -> orderedRecords
                    }
                }

                "updatedAt" -> {
                    when (orderType) {
                        OrderType.ASC -> orderedRecords.sortedBy { record -> record.updatedAt() }
                        OrderType.DESC -> orderedRecords.sortedByDescending { record -> record.updatedAt() }
                        else -> orderedRecords
                    }
                }

                else -> orderedRecords
            }
        }

        return records
    }

    fun applyPagination(records: List<FederalEntity>, limit: Int?, offset: Int?): List<FederalEntity> {
        if (limit == null || offset == null)
            return records

        return records.subList(offset, offset + limit)
    }
}
