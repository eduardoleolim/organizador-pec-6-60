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
        return records.filter { record ->
            val orFilterPassed = criteria.orFilters.filters.any { filterPassed(record, it) == true }
            val andFilterPassed = criteria.andFilters.filters.all { filterPassed(record, it) == true }

            when {
                !criteria.hasAndFilters() && !criteria.hasOrFilters() -> true
                !criteria.hasAndFilters() -> orFilterPassed
                !criteria.hasOrFilters() -> andFilterPassed
                criteria.isOrCriteria -> orFilterPassed || andFilterPassed
                else -> orFilterPassed && andFilterPassed
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
        criteria: Criteria
    ): List<FederalEntity> {
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
