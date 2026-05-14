# Criteria Pattern

Pattern for building dynamic queries with filters, ordering, and pagination. Language-agnostic guide for implementing and testing.

## Quick Start

1. **Understand the architecture**: Read `concepts/architecture.md`
2. **Choose your language**: See implementations in Kotlin, TypeScript, or Python
3. **Implement the pattern**: Use the data structures and parser interface
4. **Test thoroughly**: Follow the 3-level testing strategy in `testing/strategy.md`

## Structure

```
criteria-pattern/
├── concepts/           # Architecture & concepts
├── implementations/    # Code examples by language
├── testing/           # Testing strategy & templates
└── examples/          # Real-world use cases
```

## When to Use

- Building search/filter APIs
- Implementing repositories with dynamic queries
- Need to serialize/transport query criteria
- Multiple filter operators (AND/OR logic)

## When NOT to Use

- Simple CRUD with fixed queries
- No filtering/search requirements
- Static queries that don't need flexibility

---

For detailed information, start with `concepts/architecture.md`.