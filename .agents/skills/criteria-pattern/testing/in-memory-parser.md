# In-Memory Parser Implementation

The In-Memory parser applies criteria directly to in-memory collections. It's the **reference implementation** used for equivalence testing.

## Interface Contract

```kotlin
interface InMemoryCriteriaParser<T> {
    fun filter(records: List<T>, criteria: Criteria): List<T>
    fun order(records: List<T>, criteria: Criteria): List<T>
    fun paginate(records: List<T>, limit: Int?, offset: Int?): List<T>
    fun apply(criteria: Criteria, records: List<T>): List<T>
}
```

## Implementation Structure

### 1. Filter Implementation

```kotlin
object InMemoryFederalEntitiesCriteriaParser {

    fun applyFilters(records: List<FederalEntity>, criteria: Criteria): List<FederalEntity> {
        return records.filter { record ->
            when (val filters = criteria.filters) {
                is EmptyFilters -> true
                is SingleFilter -> filterPassed(record, filters.filter) ?: true
                is MultipleFilters -> filtersPassed(record, filters) ?: true
            }
        }
    }

    private fun filtersPassed(record: FederalEntity, filters: MultipleFilters): Boolean? {
        if (filters.isEmpty()) return null

        val conditionResults = filters.filters.mapNotNull {
            when (it) {
                is SingleFilter -> filterPassed(record, it.filter)
                is MultipleFilters -> filtersPassed(record, it)
                else -> null
            }
        }

        if (conditionResults.isEmpty()) return null

        return when (filters.operator) {
            FiltersOperator.AND -> conditionResults.all { it }
            FiltersOperator.OR -> conditionResults.any { it }
        }
    }

    private fun filterPassed(record: FederalEntity, filter: Filter): Boolean? {
        val field = FederalEntityFields.entries.firstOrNull { it.value == filter.field.value }
        val value = filter.value.value
        val operator = filter.operator

        return when (field) {
            FederalEntityFields.Id -> when (operator) {
                FilterOperator.EQUAL -> value == record.id().toString()
                FilterOperator.NOT_EQUAL -> value != record.id().toString()
                else -> null
            }
            FederalEntityFields.KeyCode -> when (operator) {
                FilterOperator.EQUAL -> value == record.keyCode()
                FilterOperator.NOT_EQUAL -> value != record.keyCode()
                FilterOperator.CONTAINS -> value.contains(record.keyCode())
                FilterOperator.NOT_CONTAINS -> !value.contains(record.keyCode())
                else -> null
            }
            // ... other fields
            null -> throw InvalidArgumentError()
        }
    }
}
```

### 2. Order Implementation

```kotlin
fun applyOrders(records: List<FederalEntity>, criteria: Criteria): List<FederalEntity> {
    return criteria.orders.orders.fold(records) { list, order ->
        val orderBy = order.orderBy.value
        val orderType = order.orderType

        val sorted = list.sortedWith(compareBy {
            when (orderBy) {
                FederalEntityFields.Id.value -> it.id()
                FederalEntityFields.KeyCode.value -> it.keyCode()
                FederalEntityFields.Name.value -> it.name()
                else -> null
            }
        })

        if (orderType == OrderType.DESC) sorted.reversed() else sorted
    }
}
```

### 3. Pagination Implementation

```kotlin
fun applyPagination(
    records: List<FederalEntity>,
    limit: Int?,
    offset: Int?
): List<FederalEntity> {
    if (limit == null && offset == null) return records
    if (limit == null) return records.drop(offset ?: 0)
    if (offset == null) return records.take(limit)

    return records.subList(offset, minOf(offset + limit, records.size))
}
```

### 4. Full Apply (Convenience)

```kotlin
fun apply(criteria: Criteria, records: List<FederalEntity>): List<FederalEntity> {
    return records
        .let { applyFilters(it, criteria) }
        .let { applyOrders(it, criteria) }
        .let { applyPagination(it, criteria.limit, criteria.offset) }
}
```

## Key Design Principles

### 1. Null Handling

```kotlin
// Return null for unsupported operators - treated as "pass"
return when (operator) {
    FilterOperator.EQUAL -> value == record.field
    else -> null  // Unsupported - filter passes
}
```

This prevents crashes; unsupported operations simply don't filter.

### 2. Field Resolution

```kotlin
// Map string field to enum/type
val field = FieldEnum.entries.firstOrNull { it.value == filter.field.value }
    ?: throw InvalidArgumentError()
```

### 3. Chaining Operations

```kotlin
// Apply in order: filter -> order -> paginate
records
    .let { applyFilters(it, criteria) }
    .let { applyOrders(it, criteria) }
    .let { applyPagination(it, criteria.limit, criteria.offset) }
```

## Type-Specific Implementation

Each entity needs its own parser because field types differ:

- `InMemoryFederalEntitiesCriteriaParser`
- `InMemoryInstrumentsCriteriaParser`
- `InMemoryUsersCriteriaParser`

## Testing Direct Against In-Memory

```kotlin
@Test
fun `single filter equals returns correct record`() {
    val records = listOf(
        createRecord("1", "AAA", "Entity A"),
        createRecord("2", "BBB", "Entity B"),
    )

    val criteria = Criteria(
        SingleFilter.equal("keyCode", "AAA"),
        Orders.none(),
        null, null
    )

    val result = InMemoryParser.apply(criteria, records)

    result.size shouldBe 1
    result[0].keyCode shouldBe "AAA"
}
```

---

Next: See `testing/equivalence-tests.md` for how to write equivalence tests.