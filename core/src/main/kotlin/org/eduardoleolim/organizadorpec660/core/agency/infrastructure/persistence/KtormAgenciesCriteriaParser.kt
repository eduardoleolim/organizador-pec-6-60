package org.eduardoleolim.organizadorpec660.core.agency.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyFields
import org.eduardoleolim.organizadorpec660.core.shared.domain.InvalidArgumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Agencies
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.AgenciesOfMunicipalities
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.StatisticTypesOfAgencies
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.time.Instant
import java.time.LocalDateTime

class KtormAgenciesCriteriaParser(
    private val database: Database,
    private val agencies: Agencies,
    private val agenciesOfMunicipalities: AgenciesOfMunicipalities,
    private val statisticTypesOfAgencies: StatisticTypesOfAgencies
) {
    fun selectQuery(criteria: Criteria): Query {
        return database.from(agencies)
            .leftJoin(agenciesOfMunicipalities, on = agencies.id eq agenciesOfMunicipalities.agencyId)
            .leftJoin(statisticTypesOfAgencies, on = agencies.id eq statisticTypesOfAgencies.agencyId)
            .selectDistinct(agencies.columns).let {
                addOrdersToQuery(it, criteria)
            }.let {
                addConditionsToQuery(it, criteria)
            }.limit(criteria.offset, criteria.limit)
    }

    fun countQuery(criteria: Criteria): Query {
        val baseQuery = selectQuery(criteria)
        val querySource = QuerySource(database, agencies, baseQuery.expression)

        return querySource.select(count())
    }

    private fun addOrdersToQuery(query: Query, criteria: Criteria): Query {
        if (!criteria.hasOrders())
            return query

        return query.orderBy(criteria.orders.orders.mapNotNull { order ->
            parseOrder(order)
        })
    }

    private fun parseOrder(order: Order): OrderByExpression? {
        val orderBy = order.orderBy.value
        val orderType = order.orderType
        val field = AgencyFields.entries.firstOrNull { it.value == orderBy }

        return when (field) {
            AgencyFields.Id -> parseOrderType(orderType, agencies.id)
            AgencyFields.Name -> parseOrderType(orderType, agencies.name)
            AgencyFields.Consecutive -> parseOrderType(orderType, agencies.consecutive)
            AgencyFields.CreatedAt -> parseOrderType(orderType, agencies.createdAt)
            AgencyFields.UpdatedAt -> parseOrderType(orderType, agencies.updatedAt)
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
        val field = AgencyFields.entries.firstOrNull { it.value == filter.field.value }
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            AgencyFields.Id -> {
                when (operator) {
                    FilterOperator.EQUAL -> agencies.id eq value
                    FilterOperator.NOT_EQUAL -> agencies.id notEq value
                    else -> null
                }
            }

            AgencyFields.Name -> {
                when (operator) {
                    FilterOperator.EQUAL -> agencies.name eq value
                    FilterOperator.NOT_EQUAL -> agencies.name notEq value
                    FilterOperator.GT -> agencies.name greater value
                    FilterOperator.GTE -> agencies.name greaterEq value
                    FilterOperator.LT -> agencies.name less value
                    FilterOperator.LTE -> agencies.name lessEq value
                    FilterOperator.CONTAINS -> agencies.name like "%$value%"
                    FilterOperator.NOT_CONTAINS -> agencies.name notLike "%$value%"
                }
            }

            AgencyFields.Consecutive -> {
                val consecutive = value.toInt()
                when (operator) {
                    FilterOperator.EQUAL -> agencies.consecutive eq consecutive
                    FilterOperator.NOT_EQUAL -> agencies.consecutive notEq consecutive
                    FilterOperator.GT -> agencies.consecutive greater consecutive
                    FilterOperator.GTE -> agencies.consecutive greaterEq consecutive
                    FilterOperator.LT -> agencies.consecutive less consecutive
                    FilterOperator.LTE -> agencies.consecutive lessEq consecutive
                    else -> null
                }
            }

            AgencyFields.CreatedAt -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> agencies.createdAt eq date
                    FilterOperator.NOT_EQUAL -> agencies.createdAt notEq date
                    FilterOperator.GT -> agencies.createdAt greater date
                    FilterOperator.GTE -> agencies.createdAt greaterEq date
                    FilterOperator.LT -> agencies.createdAt less date
                    FilterOperator.LTE -> agencies.createdAt lessEq date
                    else -> null
                }
            }

            AgencyFields.UpdatedAt -> {
                val date = LocalDateTime.from(Instant.parse(value))
                when (operator) {
                    FilterOperator.EQUAL -> agencies.updatedAt eq date
                    FilterOperator.NOT_EQUAL -> agencies.updatedAt notEq date
                    FilterOperator.GT -> agencies.updatedAt greater date
                    FilterOperator.GTE -> agencies.updatedAt greaterEq date
                    FilterOperator.LT -> agencies.updatedAt less date
                    FilterOperator.LTE -> agencies.updatedAt lessEq date
                    else -> null
                }
            }

            null -> throw InvalidArgumentError()
        }
    }
}
