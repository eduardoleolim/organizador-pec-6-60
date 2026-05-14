# Equivalence Tests

Equivalence tests verify that the **real parser** (database-specific) produces the **same results** as the **In-Memory parser** (reference).

## Why Equivalence Tests?

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Input      │     │              │     │              │
│   Data       │────►│  In-Memory   │     │    Real      │
│   +          │     │  Parser      │     │    Parser    │
│   Criteria   │     │  (trusted)   │     │  (test this) │
└──────────────┘     └──────────────┘     └──────────────┘
                            │                     │
                            └────────┬────────────┘
                                     ▼
                              ┌──────────────┐
                              │    Assert    │
                              │   Equal?     │
                              └──────────────┘
```

## Test Structure

```kotlin
class FederalEntityCriteriaEquivalenceTest : FunSpec({

    val inMemoryParser = InMemoryFederalEntitiesCriteriaParser
    lateinit var dbParser: KtormFederalEntitiesCriteriaParser

    beforeEach {
        dbParser = createDbParser()  // Setup real DB connection
    }

    // Tests go here
})
```

## Test Cases by Category

### 1. Filter Operators

```kotlin
test("single filter EQUAL returns same result as in-memory") {
    // Given
    val records = createTestRecords()
    val criteria = FederalEntityCriteria.idCriteria("1")

    // When
    val inMemoryResult = inMemoryParser.applyFilters(records, criteria)
    val dbResult = dbParser.matching(criteria)

    // Then
    inMemoryResult.map { it.id() } shouldBe dbResult.map { it.id() }
}

test("single filter CONTAINS returns same result as in-memory") {
    val records = createTestRecords()
    val criteria = Criteria(
        SingleFilter.contains(FederalEntityFields.Name.value, "test"),
        Orders.none(), null, null
    )

    val inMemoryResult = inMemoryParser.applyFilters(records, criteria)
    val dbResult = dbParser.matching(criteria)

    inMemoryResult.map { it.name() } shouldBe dbResult.map { it.name() }
}
```

### 2. Boolean Logic

```kotlin
test("AND filters return same result as in-memory") {
    val records = createTestRecords()
    val criteria = Criteria(
        AndFilters(
            SingleFilter.equal(FederalEntityFields.KeyCode.value, "01"),
            SingleFilter.contains(FederalEntityFields.Name.value, "A")
        ),
        Orders.none(), null, null
    )

    val inMemoryResult = inMemoryParser.apply(criteria, records)
    val dbResult = dbParser.matching(criteria)

    inMemoryResult.size shouldBe dbResult.size
    inMemoryResult.map { it.keyCode() } shouldContainAll dbResult.map { it.keyCode() }
}

test("OR filters return same result as in-memory") {
    val records = createTestRecords()
    val criteria = Criteria(
        OrFilters(
            SingleFilter.equal(FederalEntityFields.KeyCode.value, "01"),
            SingleFilter.equal(FederalEntityFields.KeyCode.value, "02")
        ),
        Orders.none(), null, null
    )

    val inMemoryResult = inMemoryParser.apply(criteria, records)
    val dbResult = dbParser.matching(criteria)

    inMemoryResult.map { it.keyCode() }.toSet() shouldBe dbResult.map { it.keyCode() }.toSet()
}
```

### 3. Nested Logic

```kotlin
test("AND of ORs returns same result as in-memory") {
    // (keyCode = "01" OR name CONTAINS "A") AND status = "active"
    val criteria = Criteria(
        AndFilters(
            OrFilters(
                SingleFilter.equal(FederalEntityFields.KeyCode.value, "01"),
                SingleFilter.contains(FederalEntityFields.Name.value, "A")
            ),
            SingleFilter.equal(FederalEntityFields.KeyCode.value, "01")
        ),
        Orders.none(), null, null
    )

    val inMemoryResult = inMemoryParser.apply(criteria, records)
    val dbResult = dbParser.matching(criteria)

    inMemoryResult shouldContainAll dbResult
}
```

### 4. Ordering

```kotlin
test("ORDER ASC returns same result as in-memory") {
    val criteria = Criteria(
        EmptyFilters(),
        Orders(Order.asc(FederalEntityFields.Name.value)),
        null, null
    )

    val inMemoryResult = inMemoryParser.applyOrders(records, criteria)
    val dbResult = dbParser.matching(criteria)

    inMemoryResult.map { it.name() } shouldBe dbResult.map { it.name() }
}

test("ORDER DESC returns same result as in-memory") {
    val criteria = Criteria(
        EmptyFilters(),
        Orders(Order.desc(FederalEntityFields.KeyCode.value)),
        null, null
    )

    val inMemoryResult = inMemoryParser.applyOrders(records, criteria)
    val dbResult = dbParser.matching(criteria)

    inMemoryResult.map { it.keyCode() }.reversed() shouldBe dbResult.map { it.keyCode() }
}
```

### 5. Pagination

```kotlin
test("LIMIT returns same result as in-memory") {
    val criteria = Criteria(
        EmptyFilters(),
        Orders.none(),
        2, null
    )

    val inMemoryResult = inMemoryParser.apply(criteria, records)
    val dbResult = dbParser.matching(criteria)

    inMemoryResult.size shouldBe dbResult.size
    inMemoryResult.size shouldBe 2
}

test("OFFSET returns same result as in-memory") {
    val criteria = Criteria(
        EmptyFilters(),
        Orders(Order.asc(FederalEntityFields.Id.value)),
        null, 1
    )

    val inMemoryResult = inMemoryParser.apply(criteria, records)
    val dbResult = dbParser.matching(criteria)

    inMemoryResult.map { it.id() } shouldBe dbResult.map { it.id() }
}
```

### 6. Complex Criteria (Search)

```kotlin
test("search criteria returns same result as in-memory") {
    val criteria = FederalEntityCriteria.searchCriteria(
        search = "01",
        limit = 10,
        offset = 0
    )

    val inMemoryResult = inMemoryParser.apply(criteria, allRecords)
    val dbResult = dbParser.matching(criteria)

    inMemoryResult.map { it.id() }.sorted() shouldBe dbResult.map { it.id() }.sorted()
}
```

## When to Use `.sorted()` or Sets

```kotlin
// Use sorted comparison when order matters
inMemoryResult.sortedBy { it.id() } shouldBe dbResult.sortedBy { it.id() }

// Use set comparison when order doesn't matter
inMemoryResult.map { it.id() }.toSet() shouldBe dbResult.map { it.id() }.toSet()
```

## Handling Type Differences

```kotlin
// Dates might differ in format but represent same time
// Compare timestamps, not string representation

test("date filter handles timezone correctly") {
    val criteria = Criteria(
        SingleFilter.equal(FederalEntityFields.CreatedAt.value, "2024-01-01T00:00:00Z"),
        Orders.none(), null, null
    )

    val inMemoryResult = inMemoryParser.applyFilters(records, criteria)
    val dbResult = dbParser.matching(criteria)

    // Compare by timestamp, not string
    inMemoryResult.map { it.createdAt().time } shouldBe
        dbResult.map { it.createdAt().time }
}
```

## Parameterized Tests

```kotlin
class FilterOperatorEquivalenceTest : FunSpec({
    val operators = listOf(
        FilterOperator.EQUAL to "1",
        FilterOperator.NOT_EQUAL to "1",
        FilterOperator.CONTAINS to "A",
        FilterOperator.NOT_CONTAINS to "Z"
    )

    operators.forEach { (operator, value) ->
        test("$operator filter returns same result as in-memory") {
            val criteria = Criteria(
                SingleFilter(Filter(FederalEntityFields.Name, operator, value)),
                Orders.none(), null, null
            )

            val inMemory = inMemoryParser.applyFilters(records, criteria)
            val db = dbParser.matching(criteria)

            inMemory.map { it.name() }.toSet() shouldBe db.map { it.name() }.toSet()
        }
    }
})
```

## Coverage Summary

Write equivalence tests for:
- [x] Each filter operator
- [x] AND logic
- [x] OR logic
- [x] Nested AND/OR
- [x] ASC ordering
- [x] DESC ordering
- [x] Limit only
- [x] Offset only
- [x] Combined pagination
- [x] Complex search criteria

---

Next: See `testing/integration-tests.md` for database-specific tests.