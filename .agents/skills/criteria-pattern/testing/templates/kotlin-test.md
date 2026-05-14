# Kotlin Test Template

Using JUnit 5 + Kotest.

## In-Memory Parser Test

```kotlin
package org.example.criteria

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FederalEntityInMemoryParserTest : FunSpec({

    val parser = InMemoryFederalEntitiesCriteriaParser
    val records = listOf(
        createFederalEntity("1", "01", "Entity One"),
        createFederalEntity("2", "02", "Entity Two"),
        createFederalEntity("3", "03", "Another Entity"),
    )

    test("single filter EQUAL returns correct record") {
        val criteria = Criteria(
            SingleFilter.equal("keyCode", "01"),
            Orders.none(),
            null, null
        )

        val result = parser.apply(criteria, records)

        result.size shouldBe 1
        result[0].keyCode() shouldBe "01"
    }

    test("single filter CONTAINS returns matching records") {
        val criteria = Criteria(
            SingleFilter.contains("name", "Entity"),
            Orders.none(),
            null, null
        )

        val result = parser.apply(criteria, records)

        result.size shouldBe 3
    }

    test("AND filters return records matching all conditions") {
        val criteria = Criteria(
            AndFilters(
                SingleFilter.equal("keyCode", "01"),
                SingleFilter.contains("name", "One")
            ),
            Orders.none(),
            null, null
        )

        val result = parser.apply(criteria, records)

        result.size shouldBe 1
        result[0].keyCode() shouldBe "01"
    }

    test("OR filters return records matching any condition") {
        val criteria = Criteria(
            OrFilters(
                SingleFilter.equal("keyCode", "01"),
                SingleFilter.equal("keyCode", "02")
            ),
            Orders.none(),
            null, null
        )

        val result = parser.apply(criteria, records)

        result.size shouldBe 2
    }

    test("ORDER ASC sorts correctly") {
        val criteria = Criteria(
            EmptyFilters(),
            Orders(Order.asc("name")),
            null, null
        )

        val result = parser.apply(criteria, records)

        result.map { it.name() } shouldBe listOf("Another Entity", "Entity One", "Entity Two")
    }

    test("LIMIT restricts result count") {
        val criteria = Criteria(
            EmptyFilters(),
            Orders.none(),
            2, null
        )

        val result = parser.apply(criteria, records)

        result.size shouldBe 2
    }

    test("OFFSET skips records") {
        val criteria = Criteria(
            EmptyFilters(),
            Orders(Order.asc("keyCode")),
            null, 1
        )

        val result = parser.apply(criteria, records)

        result[0].keyCode() shouldBe "02"
    }
})
```

## Equivalence Test Template

```kotlin
package org.example.criteria

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.example.persistence.KtormFederalEntitiesCriteriaParser

class FederalEntityEquivalenceTest : FunSpec({

    val inMemoryParser = InMemoryFederalEntitiesCriteriaParser
    lateinit var dbParser: KtormFederalEntitiesCriteriaParser
    lateinit var testDb: Database

    beforeEach {
        testDb = Database.connect("jdbc:sqlite::memory:")
        setupSchema(testDb)
        seedTestData(testDb)
        dbParser = KtormFederalEntitiesCriteriaParser(testDb, FederalEntities)
    }

    afterEach {
        testDb.close()
    }

    test("filter equivalence - single EQUAL") {
        val allRecords = loadAllRecords(testDb)
        val criteria = FederalEntityCriteria.idCriteria("1")

        val inMemoryResult = inMemoryParser.apply(criteria, allRecords)
        val dbResult = dbParser.matching(criteria)

        inMemoryResult.map { it.id() } shouldBe dbResult.map { it.id() }
    }

    test("filter equivalence - CONTAINS") {
        val allRecords = loadAllRecords(testDb)
        val criteria = Criteria(
            SingleFilter.contains(FederalEntityFields.Name.value, "Entity"),
            Orders.none(),
            null, null
        )

        val inMemoryResult = inMemoryParser.apply(criteria, allRecords)
        val dbResult = dbParser.matching(criteria)

        inMemoryResult.map { it.id() }.toSet() shouldBe dbResult.map { it.id() }.toSet()
    }

    test("order equivalence - ASC") {
        val allRecords = loadAllRecords(testDb)
        val criteria = Criteria(
            EmptyFilters(),
            Orders(Order.asc(FederalEntityFields.Name.value)),
            null, null
        )

        val inMemoryResult = inMemoryParser.apply(criteria, allRecords)
        val dbResult = dbParser.matching(criteria)

        inMemoryResult.map { it.name() } shouldBe dbResult.map { it.name() }
    }

    test("pagination equivalence - LIMIT") {
        val allRecords = loadAllRecords(testDb)
        val criteria = Criteria(
            EmptyFilters(),
            Orders(Order.asc(FederalEntityFields.Id.value)),
            2, null
        )

        val inMemoryResult = inMemoryParser.apply(criteria, allRecords)
        val dbResult = dbParser.matching(criteria)

        inMemoryResult.size shouldBe dbResult.size
        inMemoryResult.size shouldBe 2
    }

    test("complex criteria equivalence - search with filters") {
        val allRecords = loadAllRecords(testDb)
        val criteria = FederalEntityCriteria.searchCriteria(
            search = "Entity",
            limit = 10,
            offset = 0
        )

        val inMemoryResult = inMemoryParser.apply(criteria, allRecords)
        val dbResult = dbParser.matching(criteria)

        inMemoryResult.map { it.id() }.sorted() shouldBe dbResult.map { it.id() }.sorted()
    }
})

// Helper functions
private fun loadAllRecords(db: Database): List<FederalEntity> {
    return db.from(FederalEntities).select().map { row ->
        FederalEntity.from(
            row[FederalEntities.id],
            row[FederalEntities.keyCode],
            row[FederalEntities.name],
            // ... other fields
        )
    }
}
```

## Integration Test Template

```kotlin
package org.example.criteria

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldThrow
import org.example.persistence.KtormFederalEntitiesCriteriaParser
import org.example.domain.InvalidArgumentError

class FederalEntityIntegrationTest : FunSpec({

    lateinit var db: Database
    lateinit var parser: KtormFederalEntitiesCriteriaParser

    beforeEach {
        db = Database.connect("jdbc:sqlite:./test.db")
        setupSchema(db)
        seedTestData(db)
        parser = KtormFederalEntitiesCriteriaParser(db, FederalEntities)
    }

    afterEach {
        db.close()
    }

    test("can execute query without errors") {
        val result = parser.matching(Criteria(EmptyFilters(), Orders.none(), null, null))
        result.isNotEmpty() shouldBe true
    }

    test("count returns correct number") {
        val count = parser.count(Criteria(EmptyFilters(), Orders.none(), null, null))
        count shouldBe 3
    }

    test("valid filter executes successfully") {
        val result = parser.matching(
            Criteria(
                SingleFilter.equal("keyCode", "01"),
                Orders.none(),
                null, null
            )
        )
        result.size shouldBe 1
    }

    test("invalid field throws error") {
        shouldThrow<InvalidArgumentError> {
            parser.matching(
                Criteria(
                    SingleFilter.equal("invalid_field", "value"),
                    Orders.none(),
                    null, null
                )
            )
        }
    }

    test("empty result returns empty list") {
        val result = parser.matching(
            Criteria(
                SingleFilter.equal("id", "non-existent"),
                Orders.none(),
                null, null
            )
        )
        result shouldBe emptyList()
    }
})
```

---

Copy these templates and adjust for your entity. See other language templates in this directory.