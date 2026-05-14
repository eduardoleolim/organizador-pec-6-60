# Integration Tests

Integration tests verify that the parser works correctly with a **real database**. These tests are **storage-agnostic** - the structure applies to SQL (Postgres, MySQL, SQLite), NoSQL (MongoDB), or GraphQL.

## What to Test

| Category | What to Verify |
|----------|-----------------|
| Query Generation | SQL/Query is syntactically correct |
| Connection | Can connect and disconnect |
| Error Handling | Invalid queries handled gracefully |
| Edge Cases | Empty results, null values, etc. |

## Test Structure (Agnostic)

```kotlin
class CriteriaIntegrationTest {
    lateinit var parser: CriteriaParser  // Any DB implementation

    @BeforeEach
    fun setup() {
        parser = createParser()  // SQL, Mongo, etc.
        cleanDatabase()
        seedTestData()
    }

    @AfterEach
    fun teardown() {
        cleanDatabase()
    }
}
```

## 1. Connection Tests

```kotlin
@Test
fun `can connect to database`() {
    val connection = createConnection()
    connection.isConnected shouldBe true
}

@Test
fun `can execute simple query`() {
    val result = parser.matching(Criteria(EmptyFilters(), Orders.none(), null, null))
    result.isNotEmpty() shouldBe true  // Query executed successfully
}
```

## 2. Happy Path Tests

```kotlin
@Test
fun `simple select returns records`() {
    val criteria = Criteria(
        SingleFilter.equal("status", "active"),
        Orders.none(),
        null, null
    )

    val result = parser.matching(criteria)

    result.isNotEmpty() shouldBe true
    result.all { it.status == "active" } shouldBe true
}

@Test
fun `count query returns correct number`() {
    val criteria = Criteria(
        EmptyFilters(),
        Orders.none(),
        null, null
    )

    val count = parser.count(criteria)

    count shouldBe totalRecordsInDb
}
```

## 3. Filter Integration Tests

```kotlin
@Test
fun `equals filter generates correct query`() {
    val criteria = Criteria(
        SingleFilter.equal("id", "123"),
        Orders.none(),
        1, null
    )

    val result = parser.matching(criteria)

    // Verify query executed (don't check SQL string - ORM may change it)
    result.size shouldBe 1
    result[0].id shouldBe "123"
}

@Test
fun `contains filter works correctly`() {
    val criteria = Criteria(
        SingleFilter.contains("name", "test"),
        Orders.none(),
        null, null
    )

    val result = parser.matching(criteria)

    result.all { it.name.contains("test", ignoreCase = true) } shouldBe true
}
```

## 4. Ordering Integration Tests

```kotlin
@Test
fun `order by generates correct ordering`() {
    val criteria = Criteria(
        EmptyFilters(),
        Orders(Order.asc("createdAt")),
        null, null
    )

    val result = parser.matching(criteria)

    // Verify ordering (check actual order, not SQL)
    result.map { it.createdAt } shouldBe result.map { it.createdAt }.sorted()
}
```

## 5. Pagination Integration Tests

```kotlin
@Test
fun `limit restricts number of results`() {
    val criteria = Criteria(
        EmptyFilters(),
        Orders.none(),
        5, null
    )

    val result = parser.matching(criteria)

    result.size shouldBe 5
}

@Test
fun `offset skips correct number of records`() {
    val criteria = Criteria(
        EmptyFilters(),
        Orders(Order.asc("id")),
        null, 10
    )

    val allResults = parser.matching(Criteria(EmptyFilters(), Orders.none(), null, null))
    val pagedResults = parser.matching(criteria)

    pagedResults[0].id shouldBe allResults[10].id
}
```

## 6. Error Handling Tests

```kotlin
@Test
fun `invalid field name throws appropriate error`() {
    val criteria = Criteria(
        SingleFilter.equal("invalid_field", "value"),
        Orders.none(),
        null, null
    )

    shouldThrow<InvalidArgumentError> {
        parser.matching(criteria)
    }
}

@Test
fun `invalid operator throws appropriate error`() {
    val criteria = Criteria(
        SingleFilter(
            Filter(
                Field("id"),
                FilterOperator.fromValue("INVALID"),
                Value("1")
            )
        ),
        Orders.none(),
        null, null
    )

    shouldThrow<InvalidArgumentError> {
        parser.matching(criteria)
    }
}
```

## 7. Edge Case Tests

```kotlin
@Test
fun `empty result returns empty list`() {
    val criteria = Criteria(
        SingleFilter.equal("id", "non-existent-id"),
        Orders.none(),
        null, null
    )

    val result = parser.matching(criteria)

    result shouldBe emptyList()
}

@Test
fun `empty filters returns all records`() {
    val criteria = Criteria(
        EmptyFilters(),
        Orders.none(),
        null, null
    )

    val result = parser.matching(criteria)

    result.size shouldBe totalRecordsInDb
}
```

## DB-Specific Considerations

### SQLite
```kotlin
fun createSqliteParser(): KtormCriteriaParser {
    val db = Database.connect("jdbc:sqlite::memory:")
    return KtormCriteriaParser(db)
}
```

### PostgreSQL
```kotlin
fun createPostgresParser(): KtormCriteriaParser {
    val db = Database.connect(
        url = "jdbc:postgresql://localhost:5432/test",
        user = "test",
        password = "test"
    )
    return KtormCriteriaParser(db)
}
```

### MongoDB
```kotlin
fun createMongoParser(): MongoCriteriaParser {
    val client = MongoClients.create("mongodb://localhost:27017")
    val db = client.getDatabase("test")
    return MongoCriteriaParser(db)
}
```

## Performance Testing (Optional)

```kotlin
@Test
fun `query performs within acceptable time`() {
    val start = System.currentTimeMillis()

    val criteria = Criteria(
        AndFilters(listOf(
            SingleFilter.equal("status", "active"),
            SingleFilter.contains("name", "test")
        )),
        Orders(Order.desc("createdAt")),
        100, 0
    )

    val result = parser.matching(criteria)

    val elapsed = System.currentTimeMillis() - start

    // Adjust threshold based on data volume
    elapsed shouldBeLessThan 1000  // 1 second max
}
```

## Best Practices

1. **Test behavior, not SQL string** - The generated SQL may vary between ORM versions
2. **Use real data types** - Don't mock the DB
3. **Clean state before/after** - Use `@BeforeEach` and `@AfterEach`
4. **Test one thing per test** - Keep tests focused
5. **Use descriptive names** - `filter_equals_returns_correct_records`

## When to Run Integration Tests

- **CI/CD**: Run on every commit (slower, but ensures correctness)
- **Pre-release**: Full integration suite
- **Local development**: Run subset that's relevant to your changes

---

The testing strategy is complete. See `testing/templates/` for language-specific test templates.