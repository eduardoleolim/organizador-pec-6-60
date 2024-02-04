package org.eduardoleolim.organizadorpec660.core.instrument.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.*

object InstrumentCriteria {
    fun idCriteria(id: String) = Criteria(
        SingleFilter(Filter(FilterField("id"), FilterOperator.EQUAL, FilterValue(id))),
        Orders.none(),
        1,
        null
    )

    fun anotherInstrumentCriteria(
        instrumentId: String,
        statisticYear: Int,
        statisticMonth: Int,
        consecutive: String,
        instrumentTypeId: String,
        statisticTypeId: String,
        municipalityId: String
    ) = Criteria(
        AndFilters(
            listOf(
                SingleFilter(Filter(FilterField("id"), FilterOperator.NOT_EQUAL, FilterValue(instrumentId))),
                SingleFilter(
                    Filter(
                        FilterField("statisticYear"),
                        FilterOperator.EQUAL,
                        FilterValue(statisticYear.toString())
                    )
                ),
                SingleFilter(
                    Filter(
                        FilterField("statisticMonth"),
                        FilterOperator.EQUAL,
                        FilterValue(statisticMonth.toString())
                    )
                ),
                SingleFilter(Filter(FilterField("consecutive"), FilterOperator.EQUAL, FilterValue(consecutive))),
                SingleFilter(
                    Filter(
                        FilterField("instrumentTypeId"),
                        FilterOperator.EQUAL,
                        FilterValue(instrumentTypeId)
                    )
                ),
                SingleFilter(
                    Filter(
                        FilterField("statisticTypeId"),
                        FilterOperator.EQUAL,
                        FilterValue(statisticTypeId)
                    )
                ),
                SingleFilter(Filter(FilterField("municipalityId"), FilterOperator.EQUAL, FilterValue(municipalityId)))
            )
        ),
        Orders.none(),
        1,
        null
    )
}
