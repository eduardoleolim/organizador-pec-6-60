package org.eduardoleolim.organizadorpec660.core.instrument.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentFields
import org.eduardoleolim.organizadorpec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import org.ktorm.schema.IntSqlType
import org.ktorm.schema.VarcharSqlType
import java.time.Instant
import java.time.LocalDateTime

class KtormInstrumentsCriteriaParser(
    private val database: Database,
    private val instruments: Instruments,
    private val instrumentFiles: InstrumentFiles,
    private val agencies: Agencies,
    private val statisticTypes: StatisticTypes,
    private val municipalities: Municipalities
) {
    fun selectQuery(criteria: Criteria): Query {
        return database.from(instruments)
            .innerJoin(instrumentFiles, on = instruments.instrumentFileId eq instrumentFiles.id)
            .innerJoin(agencies, on = instruments.agencyId eq agencies.id)
            .innerJoin(municipalities, on = instruments.municipalityId eq municipalities.id)
            .innerJoin(statisticTypes, on = instruments.statisticTypeId eq statisticTypes.id)
            .select().let {
                addOrdersToQuery(it, criteria)
            }.let {
                addConditionsToQuery(it, criteria)
            }.limit(criteria.offset, criteria.limit)
    }

    fun countQuery(criteria: Criteria): Query {
        val baseQuery = selectQuery(criteria)
        val querySource = QuerySource(database, instruments, baseQuery.expression)

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
        val field = InstrumentFields.entries.firstOrNull { it.value == orderBy }
        val column = when (field) {
            InstrumentFields.Id -> instruments.id
            InstrumentFields.StatisticYear -> instruments.statisticYear
            InstrumentFields.StatisticMonth -> instruments.statisticMonth
            InstrumentFields.AgencyConsecutive -> agencies.consecutive
            InstrumentFields.Saved -> instruments.saved
            InstrumentFields.AgencyId -> instruments.agencyId
            InstrumentFields.StatisticTypeId -> instruments.statisticTypeId
            InstrumentFields.MunicipalityId -> instruments.municipalityId
            InstrumentFields.CreatedAt -> instruments.createdAt
            InstrumentFields.UpdatedAt -> instruments.updatedAt
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
        val field = InstrumentFields.entries.firstOrNull { it.value == filter.field.value }
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            InstrumentFields.Id -> {
                when (operator) {
                    FilterOperator.EQUAL -> instruments.id eq value
                    FilterOperator.NOT_EQUAL -> instruments.id notEq value
                    else -> null
                }
            }

            InstrumentFields.StatisticYear -> {
                val statisticYear = value.toIntOrNull()

                when (operator) {
                    FilterOperator.EQUAL -> instruments.statisticYear.cast(VarcharSqlType) eq value
                    FilterOperator.NOT_EQUAL -> instruments.statisticYear.cast(VarcharSqlType) notEq value
                    FilterOperator.GT -> statisticYear?.let { instruments.statisticYear greater it }
                    FilterOperator.GTE -> statisticYear?.let { instruments.statisticYear greaterEq it }
                    FilterOperator.LT -> statisticYear?.let { instruments.statisticYear less it }
                    FilterOperator.LTE -> statisticYear?.let { instruments.statisticYear lessEq it }
                    else -> null
                }
            }

            InstrumentFields.StatisticMonth -> {
                value.toIntOrNull()?.let { month ->
                    when (operator) {
                        FilterOperator.EQUAL -> instruments.statisticMonth eq month
                        FilterOperator.NOT_EQUAL -> instruments.statisticMonth notEq month
                        else -> null
                    }
                }
            }

            InstrumentFields.AgencyConsecutive -> {
                val consecutive = value.toIntOrNull()

                when (operator) {
                    FilterOperator.EQUAL -> agencies.consecutive eq value
                    FilterOperator.NOT_EQUAL -> agencies.consecutive notEq value
                    FilterOperator.GT -> consecutive?.let { agencies.consecutive.cast(IntSqlType) greater it }
                    FilterOperator.GTE -> consecutive?.let { agencies.consecutive.cast(IntSqlType) greaterEq it }
                    FilterOperator.LT -> consecutive?.let { agencies.consecutive.cast(IntSqlType) less it }
                    FilterOperator.LTE -> consecutive?.let { agencies.consecutive.cast(IntSqlType) lessEq it }
                    FilterOperator.CONTAINS -> agencies.consecutive like "%$value%"
                    FilterOperator.NOT_CONTAINS -> agencies.consecutive notLike "%$value%"
                }
            }

            InstrumentFields.Saved -> {
                when (operator) {
                    FilterOperator.EQUAL -> instruments.saved eq value.toBoolean()
                    FilterOperator.NOT_EQUAL -> instruments.saved notEq value.toBoolean()
                    else -> null
                }
            }

            InstrumentFields.AgencyId -> {
                when (operator) {
                    FilterOperator.EQUAL -> instruments.agencyId eq value
                    FilterOperator.NOT_EQUAL -> instruments.agencyId notEq value
                    else -> null
                }
            }

            InstrumentFields.StatisticTypeId -> {
                when (operator) {
                    FilterOperator.EQUAL -> instruments.statisticTypeId eq value
                    FilterOperator.NOT_EQUAL -> instruments.statisticTypeId notEq value
                    else -> null
                }
            }

            InstrumentFields.MunicipalityId -> {
                when (operator) {
                    FilterOperator.EQUAL -> instruments.municipalityId eq value
                    FilterOperator.NOT_EQUAL -> instruments.municipalityId notEq value
                    else -> null
                }
            }

            InstrumentFields.CreatedAt -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> instruments.createdAt eq date
                    FilterOperator.NOT_EQUAL -> instruments.createdAt notEq date
                    FilterOperator.GT -> instruments.createdAt greater date
                    FilterOperator.GTE -> instruments.createdAt greaterEq date
                    FilterOperator.LT -> instruments.createdAt less date
                    FilterOperator.LTE -> instruments.createdAt lessEq date
                    else -> null
                }
            }

            InstrumentFields.UpdatedAt -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> instruments.updatedAt eq date
                    FilterOperator.NOT_EQUAL -> instruments.updatedAt notEq date
                    FilterOperator.GT -> instruments.updatedAt greater date
                    FilterOperator.GTE -> instruments.updatedAt greaterEq date
                    FilterOperator.LT -> instruments.updatedAt less date
                    FilterOperator.LTE -> instruments.updatedAt lessEq date
                    else -> null
                }
            }

            null -> throw InvalidArgumentError()
        }
    }
}
