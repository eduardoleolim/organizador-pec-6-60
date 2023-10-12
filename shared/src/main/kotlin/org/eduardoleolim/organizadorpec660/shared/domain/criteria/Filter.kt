package org.eduardoleolim.organizadorpec660.shared.domain.criteria

import org.eduardoleolim.organizadorpec660.shared.domain.InvalidArgumentError

class Filter(val field: FilterField, val operator: FilterOperator, val value: FilterValue) {
    fun serialize() = String.format("%s.%s.%s", field.value, operator.value, value.value)

    companion object {
        fun create(field: String, operator: String, value: String) =
            Filter(FilterField(field), FilterOperator.fromValue(operator.uppercase()), FilterValue(value))

        fun fromValues(values: HashMap<String, String>): Filter {
            val field = values["field"]
            val operator = values["operator"]
            val value = values["value"]

            if (field == null || operator == null || value == null)
                throw InvalidArgumentError()

            return Filter(FilterField(field), FilterOperator.fromValue(operator), FilterValue(value))
        }
    }
}
