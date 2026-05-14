# Kotlin Implementation

Based on the pattern from this project's codebase.

## Data Structures

### Filter Operators

```kotlin
enum class FilterOperator(val value: String) {
    EQUAL("="),
    NOT_EQUAL("!="),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    CONTAINS("CONTAINS"),
    NOT_CONTAINS("NOT_CONTAINS");

    companion object {
        fun fromValue(value: String) = when (value) {
            "=" -> EQUAL
            "!=" -> NOT_EQUAL
            ">" -> GT
            ">=" -> GTE
            "<" -> LT
            "<=" -> LTE
            "CONTAINS" -> CONTAINS
            "NOT_CONTAINS" -> NOT_CONTAINS
            else -> throw InvalidArgumentError()
        }
    }
}
```

### Filter

```kotlin
class Filter(
    val field: FilterField,
    val operator: FilterOperator,
    val value: FilterValue
) {
    fun serialize() = String.format(
        "%s.%s.%s",
        field.value,
        operator.value,
        value.value
    )

    companion object {
        fun create(field: String, operator: String, value: String) = Filter(
            FilterField(field),
            FilterOperator.fromValue(operator.uppercase()),
            FilterValue(value)
        )

        fun equal(field: String, value: String) = create(field, "=", value)
        fun contains(field: String, value: String) = create(field, "CONTAINS", value)
        // ... other operators
    }
}
```

### Filters (Boolean Logic)

```kotlin
sealed class Filters {
    abstract fun serialize(): String
    abstract fun isEmpty(): Boolean
    abstract val isMultiple: Boolean
}

class EmptyFilters : Filters() {
    override fun isEmpty() = true
    override val isMultiple = false
    override fun serialize() = ""
}

class SingleFilter(val filter: Filter) : Filters() {
    override fun isEmpty() = false
    override val isMultiple = false
    override fun serialize() = filter.serialize()

    companion object {
        fun equal(field: String, value: String) = SingleFilter(Filter.equal(field, value))
        fun contains(field: String, value: String) = SingleFilter(Filter.contains(field, value))
    }
}

open class MultipleFilters(
    val filters: List<Filters>,
    val operator: FiltersOperator
) : Filters() {
    constructor(vararg filters: Filters, operator: FiltersOperator) : this(filters.toList(), operator)

    override fun isEmpty() = filters.isEmpty()
    override val isMultiple = true

    override fun serialize() = filters.joinToString(separator = "^") { it.serialize() }
}

class AndFilters(filters: List<Filters>) : MultipleFilters(filters, FiltersOperator.AND) {
    constructor(vararg filters: Filters) : this(filters.toList())
}

class OrFilters(filters: List<Filters>) : MultipleFilters(filters, FiltersOperator.OR) {
    constructor(vararg filters: Filters) : this(filters.toList())
}

enum class FiltersOperator { AND, OR }
```

### Order & Orders

```kotlin
data class OrderBy(val value: String)

enum class OrderType(val type: String) {
    ASC("asc"),
    DESC("desc"),
    NONE("none");

    fun isNone() = this == NONE
}

class Order(val orderBy: OrderBy, val orderType: OrderType) {
    fun hasOrder() = !orderType.isNone()

    fun serialize() = String.format("%s.%s", orderBy.value, orderType.type)

    companion object {
        fun fromValues(orderBy: String?, orderType: String?) =
            if (orderBy == null) none()
            else Order(OrderBy(orderBy), OrderType.valueOf(orderType?.uppercase() ?: "ASC"))

        fun none() = Order(OrderBy(""), OrderType.NONE)
        fun desc(orderBy: String) = Order(OrderBy(orderBy), OrderType.DESC)
        fun asc(orderBy: String) = Order(OrderBy(orderBy), OrderType.ASC)
    }
}

class Orders(val orders: List<Order>) {
    constructor(vararg orders: Order) : this(orders.toList())

    fun serialize() = orders.joinToString(separator = "^", transform = Order::serialize)

    companion object {
        fun none() = Orders(emptyList())

        fun fromValues(orders: Array<HashMap<String, String>>) =
            Orders(orders.map { Order.fromValues(it["orderBy"], it["orderType"]) })
    }
}
```

### Criteria

```kotlin
open class Criteria(
    val filters: Filters,
    val orders: Orders,
    val limit: Int?,
    val offset: Int?
) {
    fun hasFilters() = when (filters) {
        is SingleFilter -> true
        is MultipleFilters -> filters.filters.isNotEmpty()
        is EmptyFilters -> false
    }

    fun hasOrders() = orders.orders.isNotEmpty()

    fun serialize() = String.format(
        "%s~~%s~~%s~~%s",
        filters.serialize(),
        orders.serialize(),
        limit ?: 0,
        offset ?: 0,
    )
}
```

## Domain-Specific Criteria (Example)

```kotlin
enum class FederalEntityFields(val value: String) {
    Id("id"),
    KeyCode("keyCode"),
    Name("name"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}

object FederalEntityCriteria {
    fun idCriteria(id: String) = Criteria(
        SingleFilter.equal(FederalEntityFields.Id.value, id),
        Orders.none(),
        1,
        null
    )

    fun searchCriteria(
        search: String? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = Criteria(
        search?.let {
            OrFilters(
                SingleFilter.contains(FederalEntityFields.KeyCode.value, it),
                SingleFilter.contains(FederalEntityFields.Name.value, it)
            )
        } ?: EmptyFilters(),
        Orders(Order.asc(FederalEntityFields.Name.value)),
        limit,
        offset
    )
}
```

## Parser Interface

```kotlin
interface CriteriaParser<T> {
    fun matching(criteria: Criteria): List<T>
    fun count(criteria: Criteria): Int
}
```

---

Next: See `testing/strategy.md` for testing approach.