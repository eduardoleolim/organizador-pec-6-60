package org.eduardoleolim.organizadorpec660.core.shared.domain.criteria

import org.eduardoleolim.organizadorpec660.core.shared.domain.InvalidArgumentError

class Filter(val field: FilterField, val operator: FilterOperator, val value: FilterValue) {
    fun serialize() = String.format("%s.%s.%s", field.value, operator.value, value.value)

    companion object {
        fun create(field: String, operator: String, value: String) =
            Filter(FilterField(field), FilterOperator.fromValue(operator.uppercase()), FilterValue(value))

        fun equal(field: String, value: String) = create(field, "=", value)

        fun notEqual(field: String, value: String) = create(field, "!=", value)

        fun greaterThan(field: String, value: String) = create(field, ">", value)

        fun greaterThanOrEqual(field: String, value: String) = create(field, ">=", value)

        fun lessThan(field: String, value: String) = create(field, "<", value)

        fun lessThanOrEqual(field: String, value: String) = create(field, "<=", value)

        fun contains(field: String, value: String) = create(field, "CONTAINS", value)

        fun notContains(field: String, value: String) = create(field, "NOT_CONTAINS", value)

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
