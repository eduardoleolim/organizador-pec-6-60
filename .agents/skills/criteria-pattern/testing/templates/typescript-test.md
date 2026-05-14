# TypeScript Test Template

Using Vitest.

## In-Memory Parser Test

```typescript
import { describe, it, expect, beforeEach } from 'vitest';
import { Criteria, Filters, Orders, Order, SingleFilter, AndFilters, OrFilters, EmptyFilters } from '../src/criteria';
import { InMemoryFederalEntitiesParser } from './InMemoryFederalEntitiesParser';
import { FederalEntity, FederalEntityFields } from '../src/domain';

describe('InMemoryFederalEntitiesParser', () => {
  const parser = new InMemoryFederalEntitiesParser();
  
  const records: FederalEntity[] = [
    { id: '1', keyCode: '01', name: 'Entity One' },
    { id: '2', keyCode: '02', name: 'Entity Two' },
    { id: '3', keyCode: '03', name: 'Another Entity' },
  ];

  it('single filter EQUAL returns correct record', () => {
    const criteria: Criteria = {
      filters: { type: 'single', field: FederalEntityFields.KeyCode, operator: '=', value: '01' },
      orders: { orders: [] },
      limit: undefined,
      offset: undefined,
    };

    const result = parser.apply(criteria, records);

    expect(result).toHaveLength(1);
    expect(result[0].keyCode).toBe('01');
  });

  it('single filter CONTAINS returns matching records', () => {
    const criteria: Criteria = {
      filters: { type: 'single', field: FederalEntityFields.Name, operator: 'CONTAINS', value: 'Entity' },
      orders: { orders: [] },
      limit: undefined,
      offset: undefined,
    };

    const result = parser.apply(criteria, records);

    expect(result).toHaveLength(3);
  });

  it('AND filters return records matching all conditions', () => {
    const criteria: Criteria = {
      filters: {
        type: 'multiple',
        filters: [
          { type: 'single', field: FederalEntityFields.KeyCode, operator: '=', value: '01' },
          { type: 'single', field: FederalEntityFields.Name, operator: 'CONTAINS', value: 'One' },
        ],
        operator: 'AND',
      },
      orders: { orders: [] },
      limit: undefined,
      offset: undefined,
    };

    const result = parser.apply(criteria, records);

    expect(result).toHaveLength(1);
    expect(result[0].keyCode).toBe('01');
  });

  it('OR filters return records matching any condition', () => {
    const criteria: Criteria = {
      filters: {
        type: 'multiple',
        filters: [
          { type: 'single', field: FederalEntityFields.KeyCode, operator: '=', value: '01' },
          { type: 'single', field: FederalEntityFields.KeyCode, operator: '=', value: '02' },
        ],
        operator: 'OR',
      },
      orders: { orders: [] },
      limit: undefined,
      offset: undefined,
    };

    const result = parser.apply(criteria, records);

    expect(result).toHaveLength(2);
  });

  it('ORDER ASC sorts correctly', () => {
    const criteria: Criteria = {
      filters: { type: 'empty' },
      orders: { orders: [{ orderBy: FederalEntityFields.Name, orderType: 'ASC' }] },
      limit: undefined,
      offset: undefined,
    };

    const result = parser.apply(criteria, records);

    expect(result.map(r => r.name)).toEqual(['Another Entity', 'Entity One', 'Entity Two']);
  });

  it('LIMIT restricts result count', () => {
    const criteria: Criteria = {
      filters: { type: 'empty' },
      orders: { orders: [] },
      limit: 2,
      offset: undefined,
    };

    const result = parser.apply(criteria, records);

    expect(result).toHaveLength(2);
  });

  it('OFFSET skips records', () => {
    const criteria: Criteria = {
      filters: { type: 'empty' },
      orders: { orders: [{ orderBy: FederalEntityFields.KeyCode, orderType: 'ASC' }] },
      limit: undefined,
      offset: 1,
    };

    const result = parser.apply(criteria, records);

    expect(result[0].keyCode).toBe('02');
  });
});
```

## Equivalence Test Template

```typescript
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { KnexFederalEntitiesParser } from '../src/persistence/KnexFederalEntitiesParser';
import { InMemoryFederalEntitiesParser } from './InMemoryFederalEntitiesParser';

describe('Criteria Parser Equivalence', () => {
  let dbParser: KnexFederalEntitiesParser;
  const inMemoryParser = new InMemoryFederalEntitiesParser();

  beforeEach(async () => {
    await setupTestDatabase();
    dbParser = new KnexFederalEntitiesParser(testKnex, 'federal_entities');
    await seedTestData();
  });

  afterEach(async () => {
    await cleanupTestDatabase();
  });

  it('filter equivalence - single EQUAL', async () => {
    const allRecords = await loadAllRecords();
    const criteria = createIdCriteria('1');

    const inMemoryResult = inMemoryParser.apply(criteria, allRecords);
    const dbResult = await dbParser.matching(criteria);

    expect(inMemoryResult.map(r => r.id)).toEqual(dbResult.map(r => r.id));
  });

  it('filter equivalence - CONTAINS', async () => {
    const allRecords = await loadAllRecords();
    const criteria: Criteria = {
      filters: { type: 'single', field: 'name', operator: 'CONTAINS', value: 'Entity' },
      orders: { orders: [] },
      limit: undefined,
      offset: undefined,
    };

    const inMemoryResult = inMemoryParser.apply(criteria, allRecords);
    const dbResult = await dbParser.matching(criteria);

    expect(inMemoryResult.map(r => r.id).sort()).toEqual(dbResult.map(r => r.id).sort());
  });

  it('order equivalence - ASC', async () => {
    const allRecords = await loadAllRecords();
    const criteria: Criteria = {
      filters: { type: 'empty' },
      orders: { orders: [{ orderBy: 'name', orderType: 'ASC' }] },
      limit: undefined,
      offset: undefined,
    };

    const inMemoryResult = inMemoryParser.apply(criteria, allRecords);
    const dbResult = await dbParser.matching(criteria);

    expect(inMemoryResult.map(r => r.name)).toEqual(dbResult.map(r => r.name));
  });

  it('pagination equivalence - LIMIT', async () => {
    const allRecords = await loadAllRecords();
    const criteria: Criteria = {
      filters: { type: 'empty' },
      orders: { orders: [{ orderBy: 'id', orderType: 'ASC' }] },
      limit: 2,
      offset: undefined,
    };

    const inMemoryResult = inMemoryParser.apply(criteria, allRecords);
    const dbResult = await dbParser.matching(criteria);

    expect(inMemoryResult.length).toBe(dbResult.length);
    expect(inMemoryResult.length).toBe(2);
  });

  it('complex criteria equivalence - search with filters', async () => {
    const allRecords = await loadAllRecords();
    const criteria = createSearchCriteria('Entity', 10, 0);

    const inMemoryResult = inMemoryParser.apply(criteria, allRecords);
    const dbResult = await dbParser.matching(criteria);

    expect(inMemoryResult.map(r => r.id).sort()).toEqual(dbResult.map(r => r.id).sort());
  });
});

// Helper functions
async function loadAllRecords(): Promise<FederalEntity[]> {
  return await testKnex('federal_entities').select('*');
}

function createIdCriteria(id: string): Criteria {
  return {
    filters: { type: 'single', field: 'id', operator: '=', value: id },
    orders: { orders: [] },
    limit: 1,
    offset: undefined,
  };
}
```

## Integration Test Template

```typescript
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { KnexFederalEntitiesParser } from '../src/persistence/KnexFederalEntitiesParser';

describe('Criteria Integration Tests', () => {
  let parser: KnexFederalEntitiesParser;

  beforeEach(async () => {
    await setupTestDatabase();
    parser = new KnexFederalEntitiesParser(testKnex, 'federal_entities');
    await seedTestData();
  });

  afterEach(async () => {
    await cleanupTestDatabase();
  });

  it('can execute query without errors', async () => {
    const criteria: Criteria = {
      filters: { type: 'empty' },
      orders: { orders: [] },
      limit: undefined,
      offset: undefined,
    };

    const result = await parser.matching(criteria);

    expect(result).toBeDefined();
    expect(result.length).toBeGreaterThan(0);
  });

  it('count returns correct number', async () => {
    const criteria: Criteria = {
      filters: { type: 'empty' },
      orders: { orders: [] },
      limit: undefined,
      offset: undefined,
    };

    const count = await parser.count(criteria);

    expect(count).toBe(3);
  });

  it('valid filter executes successfully', async () => {
    const criteria: Criteria = {
      filters: { type: 'single', field: 'key_code', operator: '=', value: '01' },
      orders: { orders: [] },
      limit: undefined,
      offset: undefined,
    };

    const result = await parser.matching(criteria);

    expect(result).toHaveLength(1);
  });

  it('empty result returns empty array', async () => {
    const criteria: Criteria = {
      filters: { type: 'single', field: 'id', operator: '=', value: 'non-existent' },
      orders: { orders: [] },
      limit: undefined,
      offset: undefined,
    };

    const result = await parser.matching(criteria);

    expect(result).toHaveLength(0);
  });
});
```

---

Adjust the templates for your specific entity and database setup.