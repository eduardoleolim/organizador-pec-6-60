package org.eduardoleolim.organizadorpec660.app.agency.data

sealed class InvalidAgencyArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyAgencyDataException(
    val isNameEmpty: Boolean,
    val isConsecutiveEmpty: Boolean,
    val isMunicipalityEmpty: Boolean,
    val isStatisticTypesEmpty: Boolean
) : InvalidAgencyArgumentException(
    when {
        isNameEmpty && isConsecutiveEmpty && isMunicipalityEmpty && isStatisticTypesEmpty -> "El nombre, el consecutivo, el municipio y al menos un tipo de estadística son requeridos"

        isNameEmpty && isConsecutiveEmpty && isMunicipalityEmpty -> "El nombre, el consecutivo y el municipio son requeridos"
        isNameEmpty && isConsecutiveEmpty && isStatisticTypesEmpty -> "El nombre, el consecutivo y al menos un tipo de estadística son requeridos"
        isNameEmpty && isMunicipalityEmpty && isStatisticTypesEmpty -> "El nombre, el municipio y al menos un tipo de estadística son requeridos"
        isConsecutiveEmpty && isMunicipalityEmpty && isStatisticTypesEmpty -> "El consecutivo, el municipio y al menos un tipo de estadística son requeridos"

        isNameEmpty && isConsecutiveEmpty -> "El nombre y el consecutivo son requeridos"
        isNameEmpty && isMunicipalityEmpty -> "El nombre y el municipio son requeridos"
        isNameEmpty && isStatisticTypesEmpty -> "El nombre y al menos un tipo de estadística son requeridos"
        isConsecutiveEmpty && isMunicipalityEmpty -> "El consecutivo y el municipio son requeridos"
        isConsecutiveEmpty && isStatisticTypesEmpty -> "El consecutivo y al menos un tipo de estadística son requeridos"
        isMunicipalityEmpty && isStatisticTypesEmpty -> "El municipio y al menos un tipo de estadística son requeridos"

        isNameEmpty -> "El nombre es requerido"
        isConsecutiveEmpty -> "El consecutivo es requerido"
        isMunicipalityEmpty -> "El municipio es requerido"
        isStatisticTypesEmpty -> "Al menos un tipo de estadística es requerido"

        else -> null
    }

)


