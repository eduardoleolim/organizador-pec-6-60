# Criteria Pattern Architecture

## Overview

The Criteria Pattern provides a language-agnostic structure for building dynamic queries with:
- **Filters**: Boolean logic (AND/OR) with field-based conditions
- **Orders**: Multiple field ordering (ASC/DESC)
- **Pagination**: Limit and offset

## Core Components

### 1. Criteria (Container)

The main wrapper that holds all query parameters.

```kotlin
class Criteria(
    val filters: Filters,
    val orders: Orders,
    val limit: Int?,
    val offset: Int?
)
```

### 2. Filter (Condition)

A single condition: (field, operator, value).

```kotlin
class Filter(
    val field: String,      // Field name to filter on
    val operator: FilterOperator,
    val value: String       // Value to compare against
)
```

**Common Operators:**
| Operator | Description |
|----------|-------------|
| `=` | Equality |
| `!=` | Not equal |
| `>` | Greater than |
| `>=` | Greater or equal |
| `<` | Less than |
| `<=` | Less or equal |
| `CONTAINS` | Substring match |
| `NOT_CONTAINS` | No substring match |

### 3. Filters (Boolean Logic)

Three variants to represent filter logic:

```kotlin
sealed class Filters {
    // No filter applied - always passes
    class EmptyFilters : Filters()

    // Single condition
    class SingleFilter(val filter: Filter) : Filters()

    // Multiple conditions with AND/OR
    class MultipleFilters(
        val filters: List<Filters>,
        val operator: FiltersOperator
    ) : Filters()
}

enum class FiltersOperator { AND, OR }
```

### 4. Order & Orders

Ordering configuration.

```kotlin
class Order(
    val orderBy: String,      // Field name
    val orderType: OrderType
)

enum class OrderType { ASC, DESC, NONE }

class Orders(val orders: List<Order>)
```

## Data Flow

```
User Input          Criteria Builder        Parser              Database
    │                      │                    │                    │
    │  search="test"       │                    │                    │
    │  filters=[...]       │                    │                    │
    │  orderBy="name"      │                    │                    │
    └─────────────────────►│                    │                    │
                          │ Criteria            │                    │
                          │────────────────────►│                    │
                          │                     │ SELECT query        │
                          │                     │────────────────────►│
                          │                     │        Results      │
                          │                     │◄────────────────────│
                          │         List<T>     │                    │
                          │◄────────────────────│                    │
```

## Serialization

Each component implements `serialize()` for transport/persistence:

```kotlin
// Format: filters~~orders~~limit~~offset
// Filters: field.operator.value (multiple: ^)
criteria.serialize()

// Example: "name.CONTAINS.test^status.=.active~~name.asc~~10~~0"
```

This allows:
- Storing queries in URLs
- Caching criteria in sessions
- Passing criteria between services

## Type Safety Approaches

### Option 1: String-based (Flexible)

```kotlin
class Filter(val field: String, ...)
Filter("status", EQUAL, "active")  // Runtime validation only
```

### Option 2: Enum-based (Safe)

```kotlin
enum class UserFields(val value: String) {
    ID("id"), NAME("name"), EMAIL("email")
}

Filter(UserFields.NAME, EQUAL, "John")  // Compile-time safety
```

**Recommendation**: Use enums for the field names in the application code, convert to strings when building criteria for the parser.

## Separation of Concerns

1. **Criteria Builders** (Domain-specific)
   - Create pre-configured Criteria for common queries
   - Example: `UserCriteria.idCriteria(id)`, `UserCriteria.searchCriteria(term)`

2. **Criteria Parsers** (Infrastructure-specific)
   - Convert Criteria to actual queries (SQL, Mongo, GraphQL, etc.)
   - One parser per storage backend

3. **In-Memory Parser** (Testing)
   - Apply filters/orders directly on lists
   - Used as reference for equivalence testing

---

Next: See `implementations/kotlin.md` for implementation details.