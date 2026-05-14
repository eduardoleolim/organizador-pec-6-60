# Testing Strategy for Criteria Pattern

## Three Levels of Testing

```
┌─────────────────────────────────────────────────────────────┐
│                    INTEGRATION TESTS                        │
│         DB real (SQLite, Postgres, Mongo, etc.)            │
│    Verificar: query generation, real behavior              │
└──────────────────────────┬──────────────────────────────────┘
                           ▲ Equivalence
┌──────────────────────────┴──────────────────────────────────┐
│                   EQUIVALENCE TESTS                          │
│     In-Memory Parser vs Real Parser (same results)         │
└──────────────────────────┬──────────────────────────────────┘
                           ▲ Unit
┌──────────────────────────┴──────────────────────────────────┐
│                      UNIT TESTS                              │
│         In-Memory Parser (lógica pura, sin DB)             │
│   Verificar: filtros, operadores, AND/OR, ordenamiento    │
└─────────────────────────────────────────────────────────────┘
```

## Level 1: Unit Tests (In-Memory)

### What to Test

- **Filter operators**: Each operator works correctly
- **Boolean logic**: AND/OR combinations
- **Nested logic**: AND of ORs, OR of ANDs
- **Order**: ASC/DESC on each field
- **Pagination**: limit/offset

### Why In-Memory First

- No external dependencies (no DB)
- Fast execution
- Easy to debug
- Acts as reference implementation

### Example Test Cases

| Test | Input | Expected |
|------|-------|----------|
| Single filter = | `[record1, record2]`, `name = "A"` | `[record1]` |
| Single filter CONTAINS | `[record1, record2]`, `name CONTAINS "bc"` | `[record2]` |
| AND all pass | `[r1, r2]`, `status=A AND name=B` | `[r1]` (only matches both) |
| OR any pass | `[r1, r2]`, `status=A OR name=B` | `[r1, r2]` (any matches) |
| Order ASC | `[b, a, c]`, `name ASC` | `[a, b, c]` |
| Order DESC | `[b, a, c]`, `name DESC` | `[c, b, a]` |
| Limit 2 | `[a, b, c, d]`, `limit=2` | `[a, b]` |
| Offset 1 | `[a, b, c, d]`, `offset=1` | `[b, c, d]` |

## Level 2: Equivalence Tests

### Purpose

Verify that the **real parser** (SQL, NoSQL, etc.) produces the **same results** as the **In-Memory parser**.

### Approach

```kotlin
@Test
fun `filter equivalence - single filter equals`() {
    // Given: Same input data and criteria
    val records = listOf(fe1, fe2, fe3)
    val criteria = FederalEntityCriteria.idCriteria("1")

    // When: Apply both parsers
    val inMemoryResult = inMemoryParser.filter(records, criteria)
    val dbResult = dbParser.matching(criteria)

    // Then: Results must be equal
    inMemoryResult shouldBe dbResult
}
```

### Why This Works

1. **In-Memory is trusted** - Simple logic, easy to verify manually
2. **Real parser must match** - If they differ, the real parser has a bug
3. **DB-agnostic** - Works for any storage backend

### What to Verify

- All filter operators
- All order directions
- Pagination (limit/offset)
- Complex AND/OR combinations

## Level 3: Integration Tests

### What to Test

- **Query generation**: SQL/NoSQL is syntactically correct
- **DB connectivity**: Can connect/disconnect properly
- **Error handling**: Invalid queries handled gracefully
- **Performance** (optional): Query is reasonably fast

### DB-Agnostic Approach

```python
# Test structure works for ANY backend
def test_query_generation():
    parser = create_parser(engine)  # SQLAlchemy, Knex, Prisma, etc.

    criteria = Criteria(
        filters=AndFilters([
            SingleFilter.equal("status", "active"),
            SingleFilter.contains("name", "test")
        ]),
        orders=Orders([Order.asc("created_at")]),
        limit=10,
        offset=0
    )

    result = parser.matching(criteria)
    assert result is not None  # Query executed without error
```

### Categories

1. **Happy path**: Valid criteria produces valid query
2. **Empty results**: Query returns nothing
3. **Invalid criteria**: Malformed criteria handled
4. **Edge cases**: Nulls, special characters, etc.

## Test Organization

```
tests/
├── unit/
│   ├── filter_operators_test.py
│   ├── boolean_logic_test.py
│   ├── ordering_test.py
│   └── pagination_test.py
├── equivalence/
│   ├── test_parser_equivalence.py
│   └── test_complex_criteria.py
└── integration/
    ├── test_sql_generation.py
    ├── test_connection.py
    └── test_errors.py
```

## Coverage Checklist

### Filter Operators
- [ ] EQUAL
- [ ] NOT_EQUAL
- [ ] GT, GTE
- [ ] LT, LTE
- [ ] CONTAINS
- [ ] NOT_CONTAINS

### Boolean Logic
- [ ] Empty filters (pass through)
- [ ] Single filter
- [ ] AND with 2+ filters
- [ ] OR with 2+ filters
- [ ] Nested: AND of ORs
- [ ] Nested: OR of ANDs

### Ordering
- [ ] ASC single field
- [ ] DESC single field
- [ ] Multiple fields
- [ ] No order (default)

### Pagination
- [ ] Limit only
- [ ] Offset only
- [ ] Both limit and offset

### Edge Cases
- [ ] Empty result set
- [ ] All records match filter
- [ ] Special characters in values
- [ ] Empty/null values

---

Next: See `testing/in-memory-parser.md` for implementation details.