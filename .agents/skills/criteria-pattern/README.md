# Criteria Pattern - Language Agnostic Guide

Pattern for building dynamic queries with filters, ordering, and pagination.

## Quick Start

1. **Read architecture**: [concepts/architecture.md](concepts/architecture.md)
2. **Choose implementation**: [implementations/](implementations/)
3. **Implement & Test**: [testing/strategy.md](testing/strategy.md)

## Overview

The Criteria Pattern provides a standardized way to represent:
- **Filters**: Boolean conditions (AND/OR) with field-based operators
- **Orders**: Multiple field ordering (ASC/DESC)
- **Pagination**: Limit and offset

## Structure

```
criteria-pattern/
├── concepts/           # Architecture & design concepts
│   └── architecture.md
├── implementations/    # Code examples by language
│   ├── kotlin.md
│   ├── typescript.md
│   └── python.md
├── testing/           # Testing strategy & templates
│   ├── strategy.md
│   ├── in-memory-parser.md
│   ├── equivalence-tests.md
│   ├── integration-tests.md
│   └── templates/
│       ├── kotlin-test.md
│       ├── typescript-test.md
│       └── python-test.md
└── examples/          # Real-world use cases
    └── real-world.md
```

## Who This Is For

- Backend developers building search/filter APIs
- Developers needing query flexibility across storage backends
- Teams wanting consistent query structure across multiple services

## Key Principles

1. **Separation of concerns**: Criteria builders (domain) vs Parsers (infrastructure)
2. **In-Memory as reference**: Use for equivalence testing
3. **Storage-agnostic**: Works with SQL, NoSQL, GraphQL, etc.
4. **Serialization**: Criteria can be serialized for transport/persistence

## Languages Covered

- Kotlin (based on this project's codebase)
- TypeScript
- Python

---

For more details, see [SKILL.md](SKILL.md).