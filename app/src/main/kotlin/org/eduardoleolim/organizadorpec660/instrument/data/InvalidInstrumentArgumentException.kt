package org.eduardoleolim.organizadorpec660.instrument.data

sealed class InvalidInstrumentArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyInstrumentDataException(
    val isStatisticYearUnselected: Boolean,
    val isStatisticMonthUnselected: Boolean,
    val isMunicipalityUnselected: Boolean,
    val isAgencyUnselected: Boolean,
    val isStatisticTypeUnselected: Boolean,
    val isInstrumentFileUnselected: Boolean,
) : InvalidInstrumentArgumentException(
    when {
        isStatisticYearUnselected && isStatisticMonthUnselected && isMunicipalityUnselected && isAgencyUnselected && isStatisticTypeUnselected && isInstrumentFileUnselected ->
            "The year, month, municipality, agency, statistic type, and instrument file are required"

        isStatisticYearUnselected && isStatisticMonthUnselected -> "The year and month are required"
        isMunicipalityUnselected && isAgencyUnselected -> "The municipality and agency are required"
        isStatisticTypeUnselected && isInstrumentFileUnselected -> "The statistic type and instrument file are required"
        isStatisticYearUnselected -> "The year is required"
        isStatisticMonthUnselected -> "The month is required"
        isMunicipalityUnselected -> "The municipality is required"
        isAgencyUnselected -> "The agency is required"
        isStatisticTypeUnselected -> "The statistic type is required"
        isInstrumentFileUnselected -> "The instrument file is required"
        else -> "No data is missing"
    }
)
