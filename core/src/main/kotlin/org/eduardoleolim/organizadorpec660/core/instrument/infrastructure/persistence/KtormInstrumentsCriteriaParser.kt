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
import java.time.Instant
import java.time.LocalDateTime

class KtormInstrumentsCriteriaParser(
    private val database: Database,
    private val instruments: Instruments,
    private val instrumentFiles: InstrumentFiles,
    private val agencies: Agencies,
    private val statisticTypes: StatisticTypes,
    private val federalEntities: FederalEntities,
    private val municipalities: Municipalities
) {
    fun selectQuery(criteria: Criteria): Query {
        return database.from(instruments)
            .innerJoin(instrumentFiles, on = instruments.instrumentFileId eq instrumentFiles.id)
            .innerJoin(agencies, on = instruments.agencyId eq agencies.id)
            .innerJoin(municipalities, on = instruments.municipalityId eq municipalities.id)
            .innerJoin(statisticTypes, on = instruments.statisticTypeId eq statisticTypes.id)
            .innerJoin(federalEntities, on = municipalities.federalEntityId eq federalEntities.id)
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
            InstrumentFields.StatisticTypeId -> statisticTypes.id
            InstrumentFields.StatisticTypeKeyCode -> statisticTypes.keyCode
            InstrumentFields.StatisticTypeName -> statisticTypes.name
            InstrumentFields.FederalEntityId -> federalEntities.id
            InstrumentFields.FederalEntityKeyCode -> federalEntities.keyCode
            InstrumentFields.FederalEntityName -> federalEntities.name
            InstrumentFields.MunicipalityId -> municipalities.id
            InstrumentFields.MunicipalityKeyCode -> municipalities.keyCode
            InstrumentFields.MunicipalityName -> municipalities.name
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
            InstrumentFields.Id -> parseIdFilter(operator, value)
            InstrumentFields.StatisticYear -> value.toIntOrNull()?.let { parseStatisticYearFilter(operator, it) }
            InstrumentFields.StatisticMonth -> value.toIntOrNull()?.let { parseStatisticMonthFilter(operator, it) }
            InstrumentFields.Saved -> parseSavedFilter(operator, value.toBoolean())
            InstrumentFields.AgencyId -> parseAgencyIdFilter(operator, value)
            InstrumentFields.AgencyConsecutive -> parseAgencyConsecutiveFilter(operator, value)
            InstrumentFields.StatisticTypeId -> parseStatisticTypeIdFilter(operator, value)
            InstrumentFields.StatisticTypeKeyCode -> parseStatisticTypeKeyCodeFilter(operator, value)
            InstrumentFields.StatisticTypeName -> parseStatisticTypeNameFilter(operator, value)
            InstrumentFields.FederalEntityId -> parseFederalEntityIdFilter(operator, value)
            InstrumentFields.FederalEntityKeyCode -> parseFederalEntityKeyCodeFilter(operator, value)
            InstrumentFields.FederalEntityName -> parseFederalEntityNameFilter(operator, value)
            InstrumentFields.MunicipalityId -> parseMunicipalityIdFilter(operator, value)
            InstrumentFields.MunicipalityKeyCode -> parseMunicipalityKeyCodeFilter(operator, value)
            InstrumentFields.MunicipalityName -> parseMunicipalityNameFilter(operator, value)
            InstrumentFields.CreatedAt -> parseCreatedAtFilter(operator, LocalDateTime.from(Instant.parse(value)))
            InstrumentFields.UpdatedAt -> parseUpdatedAtFilter(operator, LocalDateTime.from(Instant.parse(value)))
            null -> throw InvalidArgumentError()
        }
    }

    private fun parseIdFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> instruments.id eq value
            FilterOperator.NOT_EQUAL -> instruments.id notEq value
            else -> null
        }
    }

    private fun parseStatisticYearFilter(operator: FilterOperator, value: Int): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> instruments.statisticYear eq value
            FilterOperator.NOT_EQUAL -> instruments.statisticYear notEq value
            FilterOperator.GT -> instruments.statisticYear greater value
            FilterOperator.GTE -> instruments.statisticYear greaterEq value
            FilterOperator.LT -> instruments.statisticYear less value
            FilterOperator.LTE -> instruments.statisticYear lessEq value
            else -> null
        }
    }

    private fun parseStatisticMonthFilter(operator: FilterOperator, value: Int): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> instruments.statisticMonth eq value
            FilterOperator.NOT_EQUAL -> instruments.statisticMonth notEq value
            else -> null
        }
    }

    private fun parseSavedFilter(operator: FilterOperator, value: Boolean): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> instruments.saved eq value
            FilterOperator.NOT_EQUAL -> instruments.saved notEq value
            else -> null
        }
    }

    private fun parseAgencyIdFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> instruments.agencyId eq value
            FilterOperator.NOT_EQUAL -> instruments.agencyId notEq value
            else -> null
        }
    }

    private fun parseAgencyConsecutiveFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean>? {
        val consecutive = value.toIntOrNull()

        return when (operator) {
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

    private fun parseStatisticTypeIdFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> instruments.statisticTypeId eq value
            FilterOperator.NOT_EQUAL -> instruments.statisticTypeId notEq value
            else -> null
        }
    }

    private fun parseStatisticTypeKeyCodeFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean> {
        return when (operator) {
            FilterOperator.EQUAL -> statisticTypes.keyCode eq value
            FilterOperator.NOT_EQUAL -> statisticTypes.keyCode notEq value
            FilterOperator.GT -> statisticTypes.keyCode greater value
            FilterOperator.GTE -> statisticTypes.keyCode greaterEq value
            FilterOperator.LT -> statisticTypes.keyCode less value
            FilterOperator.LTE -> statisticTypes.keyCode lessEq value
            FilterOperator.CONTAINS -> statisticTypes.keyCode like "%$value%"
            FilterOperator.NOT_CONTAINS -> statisticTypes.keyCode notLike "%$value%"
        }
    }

    private fun parseStatisticTypeNameFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean> {
        return when (operator) {
            FilterOperator.EQUAL -> statisticTypes.name eq value
            FilterOperator.NOT_EQUAL -> statisticTypes.name notEq value
            FilterOperator.GT -> statisticTypes.name greater value
            FilterOperator.GTE -> statisticTypes.name greaterEq value
            FilterOperator.LT -> statisticTypes.name less value
            FilterOperator.LTE -> statisticTypes.name lessEq value
            FilterOperator.CONTAINS -> statisticTypes.name like "%$value%"
            FilterOperator.NOT_CONTAINS -> statisticTypes.name notLike "%$value%"
        }
    }

    private fun parseFederalEntityIdFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> federalEntities.id eq value
            FilterOperator.NOT_EQUAL -> federalEntities.id notEq value
            else -> null
        }
    }

    private fun parseFederalEntityKeyCodeFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean> {
        return when (operator) {
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

    private fun parseFederalEntityNameFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean> {
        return when (operator) {
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

    private fun parseMunicipalityIdFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> instruments.municipalityId eq value
            FilterOperator.NOT_EQUAL -> instruments.municipalityId notEq value
            else -> null
        }
    }

    private fun parseMunicipalityKeyCodeFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean> {
        return when (operator) {
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

    private fun parseMunicipalityNameFilter(operator: FilterOperator, value: String): ColumnDeclaring<Boolean> {
        return when (operator) {
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

    private fun parseCreatedAtFilter(operator: FilterOperator, value: LocalDateTime): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> instruments.createdAt eq value
            FilterOperator.NOT_EQUAL -> instruments.createdAt notEq value
            FilterOperator.GT -> instruments.createdAt greater value
            FilterOperator.GTE -> instruments.createdAt greaterEq value
            FilterOperator.LT -> instruments.createdAt less value
            FilterOperator.LTE -> instruments.createdAt lessEq value
            else -> null
        }
    }

    private fun parseUpdatedAtFilter(operator: FilterOperator, value: LocalDateTime): ColumnDeclaring<Boolean>? {
        return when (operator) {
            FilterOperator.EQUAL -> instruments.updatedAt eq value
            FilterOperator.NOT_EQUAL -> instruments.updatedAt notEq value
            FilterOperator.GT -> instruments.updatedAt greater value
            FilterOperator.GTE -> instruments.updatedAt greaterEq value
            FilterOperator.LT -> instruments.updatedAt less value
            FilterOperator.LTE -> instruments.updatedAt lessEq value
            else -> null
        }
    }
}
