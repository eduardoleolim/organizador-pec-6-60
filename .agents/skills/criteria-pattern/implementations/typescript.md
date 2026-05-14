# TypeScript Implementation

## Types

### Filter Operators

```typescript
type FilterOperator =
  | '='
  | '!='
  | '>'
  | '>='
  | '<'
  | '<='
  | 'CONTAINS'
  | 'NOT_CONTAINS';

const filterOperators: Record<FilterOperator, string> = {
  '=': '=',
  '!=': '!=',
  '>': '>',
  '>=': '>=',
  '<': '<',
  '<=': '<=',
  'CONTAINS': 'CONTAINS',
  'NOT_CONTAINS': 'NOT_CONTAINS',
};
```

### Filter & Filters

```typescript
interface Filter<T extends string = string> {
  field: T;
  operator: FilterOperator;
  value: string;
}

type Filters<T extends string = string> =
  | EmptyFilters
  | SingleFilter<T>
  | MultipleFilters<T>;

interface EmptyFilters {
  type: 'empty';
}

interface SingleFilter<T extends string = string> {
  type: 'single';
  filter: Filter<T>;
}

interface MultipleFilters<T extends string = string> {
  type: 'multiple';
  filters: Filters<T>[];
  operator: 'AND' | 'OR';
}
```

### Order & Orders

```typescript
type OrderType = 'ASC' | 'DESC' | 'NONE';

interface Order<T extends string = string> {
  orderBy: T;
  orderType: OrderType;
}

interface Orders<T extends string = string> {
  orders: Order<T>[];
}
```

### Criteria

```typescript
interface Criteria<T extends string = string> {
  filters: Filters<T>;
  orders: Orders<T>;
  limit?: number;
  offset?: number;
}
```

## Builder Functions

```typescript
// Filter builders
function equal<T extends string>(field: T, value: string): SingleFilter<T> {
  return { type: 'single', filter: { field, operator: '=', value } };
}

function contains<T extends string>(field: T, value: string): SingleFilter<T> {
  return { type: 'single', filter: { field, operator: 'CONTAINS', value } };
}

// Combine filters
function and<T extends string>(...filters: Filters<T>[]): MultipleFilters<T> {
  return { type: 'multiple', filters, operator: 'AND' };
}

function or<T extends string>(...filters: Filters<T>[]): MultipleFilters<T> {
  return { type: 'multiple', filters, operator: 'OR' };
}

// Empty filter
const emptyFilters: EmptyFilters = { type: 'empty' };
```

## Criteria Builders (Domain-Specific)

```typescript
// Example with enum-like fields
const FederalEntityFields = {
  Id: 'id' as const,
  KeyCode: 'keyCode' as const,
  Name: 'name' as const,
};

type FederalEntityField = typeof FederalEntityFields[keyof typeof FederalEntityFields];

function federalEntityIdCriteria(id: string): Criteria<FederalEntityField> {
  return {
    filters: equal(FederalEntityFields.Id, id),
    orders: { orders: [] },
    limit: 1,
    offset: undefined,
  };
}

function federalEntitySearchCriteria(
  search?: string,
  limit?: number,
  offset?: number
): Criteria<FederalEntityField> {
  return {
    filters: search
      ? or(
          contains(FederalEntityFields.KeyCode, search),
          contains(FederalEntityFields.Name, search)
        )
      : emptyFilters,
    orders: {
      orders: [{ orderBy: FederalEntityFields.Name, orderType: 'ASC' }],
    },
    limit,
    offset,
  };
}
```

## Parser Interface

```typescript
interface CriteriaParser<T, Field extends string = string> {
  matching(criteria: Criteria<Field>): Promise<T[]>;
  count(criteria: Criteria<Field>): Promise<number>;
}
```

## Example Implementation (SQL)

```typescript
import { Knex } from 'knex';

class KnexCriteriaParser<T> implements CriteriaParser<T> {
  constructor(
    private knex: Knex,
    private tableName: string,
    private fieldMap: Record<string, string>
  ) {}

  async matching(criteria: Criteria): Promise<T[]> {
    let query = this.knex(this.tableName).distinct();

    // Apply filters
    query = this.applyFilters(query, criteria.filters);

    // Apply orders
    if (criteria.orders.orders.length > 0) {
      const orderClauses = criteria.orders.orders.map(o => ({
        column: this.fieldMap[o.orderBy] || o.orderBy,
        order: o.orderType.toLowerCase(),
      }));
      query = query.orderBy(orderClauses);
    }

    // Apply pagination
    if (criteria.limit) query = query.limit(criteria.limit);
    if (criteria.offset) query = query.offset(criteria.offset);

    return query;
  }

  private applyFilters(query: Knex.QueryBuilder, filters: Filters): Knex.QueryBuilder {
    if (filters.type === 'empty') return query;

    if (filters.type === 'single') {
      return query.where(this.fieldMap[filters.filter.field], this.parseOperator(filters.filter.operator), filters.filter.value);
    }

    // Multiple filters
    const conditions = filters.filters.map(f => this.buildCondition(f));
    if (filters.operator === 'AND') {
      return query.where(builder => conditions.forEach(c => builder.andWhere(c)));
    } else {
      return query.where(builder => conditions.forEach(c => builder.orWhere(c)));
    }
  }

  private parseOperator(op: FilterOperator): string {
    return op === 'CONTAINS' ? 'like' : op;
  }

  private buildCondition(filters: Filters): Knex.QueryBuilder {
    // Recursive helper for nested filters
    return this.applyFilters(this.knex.queryBuilder(), filters);
  }
}
```

---

Next: See `implementations/python.md` or go to `testing/strategy.md`.