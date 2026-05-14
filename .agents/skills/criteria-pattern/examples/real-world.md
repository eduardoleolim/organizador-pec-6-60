# Real-World Examples

## Example 1: Product Search API

### Scenario
E-commerce API with filters: category, price range, brand, in-stock, search term. Sorting by price, name, popularity. Pagination.

### Criteria Builder

```kotlin
object ProductCriteria {
    fun searchCriteria(
        search: String? = null,
        categoryId: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        brand: String? = null,
        inStock: Boolean? = null,
        sortBy: String? = null,
        sortOrder: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): Criteria {
        return Criteria(
            filters = AndFilters(
                search?.let {
                    OrFilters(
                        SingleFilter.contains(ProductFields.Name.value, it),
                        SingleFilter.contains(ProductFields.Description.value, it),
                        SingleFilter.contains(ProductFields.Brand.value, it)
                    )
                } ?: EmptyFilters(),
                categoryId?.let { SingleFilter.equal(ProductFields.CategoryId.value, it) } ?: EmptyFilters(),
                minPrice?.let { SingleFilter.greaterOrEqual(ProductFields.Price.value, it.toString()) } ?: EmptyFilters(),
                maxPrice?.let { SingleFilter.lessOrEqual(ProductFields.Price.value, it.toString()) } ?: EmptyFilters(),
                brand?.let { SingleFilter.equal(ProductFields.Brand.value, it) } ?: EmptyFilters(),
                inStock?.let { SingleFilter.equal(ProductFields.StockQuantity.value, if (it) "0" else "-1", ">") } ?: EmptyFilters()
            ),
            orders = sortBy?.let {
                Orders(Order(OrderBy(it), OrderType.valueOf(sortOrder?.uppercase() ?: "ASC")))
            } ?: Orders.none(),
            limit,
            offset
        )
    }
}
```

### API Usage

```kotlin
// GET /products?search=shoes&category= footwear&minPrice=50&sortBy=price&sortOrder=asc&limit=10&offset=0
val criteria = ProductCriteria.searchCriteria(
    search = "shoes",
    categoryId = "footwear",
    minPrice = 50.0,
    sortBy = "price",
    sortOrder = "asc",
    limit = 10,
    offset = 0
)

val products = productRepository.matching(criteria)
```

## Example 2: User Management System

### Scenario
Admin panel for user management with filters: role, status, date range, search. Sorting by name, created date.

### Criteria Builder

```kotlin
object UserCriteria {
    fun adminSearchCriteria(
        search: String? = null,
        role: String? = null,
        status: String? = null,
        createdAfter: String? = null,
        createdBefore: String? = null,
        sortBy: String = "createdAt",
        sortOrder: String = "DESC",
        limit: Int = 50,
        offset: Int = 0
    ): Criteria {
        return Criteria(
            filters = AndFilters(
                search?.let {
                    OrFilters(
                        SingleFilter.contains(UserFields.Email.value, it),
                        SingleFilter.contains(UserFields.Username.value, it),
                        SingleFilter.contains(UserFields.FullName.value, it)
                    )
                } ?: EmptyFilters(),
                role?.let { SingleFilter.equal(UserFields.Role.value, it) } ?: EmptyFilters(),
                status?.let { SingleFilter.equal(UserFields.Status.value, it) } ?: EmptyFilters(),
                createdAfter?.let { SingleFilter.greaterOrEqual(UserFields.CreatedAt.value, it) } ?: EmptyFilters(),
                createdBefore?.let { SingleFilter.lessOrEqual(UserFields.CreatedAt.value, it) } ?: EmptyFilters()
            ),
            orders = Orders(Order(OrderBy(sortBy), OrderType.valueOf(sortOrder.uppercase()))),
            limit,
            offset
        )
    }
}
```

## Example 3: Task/Issue Tracker

### Scenario
Issue tracking with filters: status, priority, assignee, project, labels. Sorting by priority, due date, created date.

### Criteria Builder

```kotlin
object IssueCriteria {
    fun dashboardCriteria(
        projectId: String,
        assigneeId: String? = null,
        status: List<String>? = null,
        priority: List<String>? = null,
        labels: List<String>? = null,
        dueDateBefore: String? = null,
        sortBy: String = "priority",
        limit: Int = 100,
        offset: Int = 0
    ): Criteria {
        return Criteria(
            filters = AndFilters(
                SingleFilter.equal(IssueFields.ProjectId.value, projectId),
                assigneeId?.let { SingleFilter.equal(IssueFields.AssigneeId.value, it) } ?: EmptyFilters(),
                status?.let {
                    OrFilters(*it.map { s -> SingleFilter.equal(IssueFields.Status.value, s) }.toTypedArray())
                } ?: EmptyFilters(),
                priority?.let {
                    OrFilters(*it.map { p -> SingleFilter.equal(IssueFields.Priority.value, p) }.toTypedArray())
                } ?: EmptyFilters(),
                dueDateBefore?.let { SingleFilter.lessOrEqual(IssueFields.DueDate.value, it) } ?: EmptyFilters()
            ),
            orders = Orders(
                Order(OrderBy(sortBy), OrderType.DESC),
                Order(OrderBy(IssueFields.DueDate.value), OrderType.ASC)
            ),
            limit,
            offset
        )
    }

    fun myIssuesCriteria(userId: String, status: String? = null): Criteria {
        return Criteria(
            filters = AndFilters(
                SingleFilter.equal(IssueFields.AssigneeId.value, userId),
                status?.let { SingleFilter.equal(IssueFields.Status.value, it) } ?: EmptyFilters()
            ),
            orders = Orders(
                Order(OrderBy(IssueFields.Priority.value), OrderType.DESC),
                Order(OrderBy(IssueFields.DueDate.value), OrderType.ASC)
            ),
            50,
            0
        )
    }
}
```

## Example 4: Reporting/Analytics

### Scenario
Generate reports with filters: date range, region, product category, aggregation level.

### Criteria Builder

```kotlin
object ReportCriteria {
    fun salesReportCriteria(
        startDate: String,
        endDate: String,
        regionId: String? = null,
        categoryId: String? = null,
        groupBy: String = "day"
    ): Criteria {
        return Criteria(
            filters = AndFilters(
                SingleFilter.greaterOrEqual(SalesFields.Date.value, startDate),
                SingleFilter.lessOrEqual(SalesFields.Date.value, endDate),
                regionId?.let { SingleFilter.equal(SalesFields.RegionId.value, it) } ?: EmptyFilters(),
                categoryId?.let { SingleFilter.equal(SalesFields.CategoryId.value, it) } ?: EmptyFilters()
            ),
            orders = Orders(Order(OrderBy(SalesFields.Date.value), OrderType.ASC)),
            null,  // No limit for reports
            null
        )
    }
}
```

## Example 5: Log/Event Search

### Scenario
Search through application logs with filters: level, timestamp range, source, message contains.

### Criteria Builder

```kotlin
object LogCriteria {
    fun searchCriteria(
        search: String? = null,
        level: String? = null,
        source: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        limit: Int = 100,
        offset: Int = 0
    ): Criteria {
        return Criteria(
            filters = AndFilters(
                search?.let {
                    OrFilters(
                        SingleFilter.contains(LogFields.Message.value, it),
                        SingleFilter.contains(LogFields.StackTrace.value, it)
                    )
                } ?: EmptyFilters(),
                level?.let { SingleFilter.equal(LogFields.Level.value, it) } ?: EmptyFilters(),
                source?.let { SingleFilter.equal(LogFields.Source.value, it) } ?: EmptyFilters(),
                startTime?.let { SingleFilter.greaterOrEqual(LogFields.Timestamp.value, it) } ?: EmptyFilters(),
                endTime?.let { SingleFilter.lessOrEqual(LogFields.Timestamp.value, it) } ?: EmptyFilters()
            ),
            orders = Orders(Order(OrderBy(LogFields.Timestamp.value), OrderType.DESC)),
            limit,
            offset
        )
    }
}
```

## Common Patterns

| Pattern | Description |
|---------|-------------|
| **Search with multiple fields** | OR of CONTAINS filters on multiple text fields |
| **Optional filters** | Use `?.let { } ?: EmptyFilters()` for nullable params |
| **Date range** | Two filters: >= start, <= end |
| **Multi-select** | OR of EQUAL filters |
| **Complex sorts** | Multiple Order objects in Orders |
| **Pagination** | Always include limit/offset for large datasets |

---

These examples show the flexibility of the Criteria pattern for different domains.