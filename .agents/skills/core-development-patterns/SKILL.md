# Core Module Development Patterns

Skill for implementing new features in the `core` module of the organizador-pec-6-60 project.

## Module Directory Structure

Each module (e.g., `federalEntity`, `municipality`, `agency`) follows this structure:

```
core/src/main/kotlin/org/eduardoleolim/organizadorpec660/[module]/
├── domain/
│   ├── [Module].kt           # Main entity + value classes
│   ├── [Module]Errors.kt    # Errors sealed class
│   ├── [Module]Repository.kt # Repository interface
│   ├── [Module]Criteria.kt   # Functions for building Criteria
│   └── [Module]ImportReader.kt (optional)
├── application/
│   ├── [Module]Response.kt  # DTO for responses
│   ├── [Module]sResponse.kt  # DTO for list responses
│   ├── Simple[Module]Response.kt (optional)
│   ├── create/
│   │   ├── Create[Module]Command.kt
│   │   ├── Create[Module]CommandHandler.kt
│   │   └── [Module]Creator.kt
│   ├── update/
│   │   ├── Update[Module]Command.kt
│   │   ├── Update[Module]CommandHandler.kt
│   │   └── [Module]Updater.kt
│   ├── delete/
│   │   ├── Delete[Module]Command.kt
│   │   ├── Delete[Module]CommandHandler.kt
│   │   └── [Module]Deleter.kt
│   ├── search/
│   │   ├── [Module]Searcher.kt
│   │   └── Search[Module]Query.kt + Handler (optional)
│   ├── searchById/
│   │   ├── Search[Module]ByIdQuery.kt
│   │   └── Search[Module]ByIdQueryHandler.kt
│   ├── searchByTerm/
│   │   ├── Search[Module]sByTermQuery.kt
│   │   └── Search[Module]sByTermQueryHandler.kt
│   └── importer/
│       ├── Import[Module]sCommand.kt (optional)
│       ├── Import[Module]sCommandHandler.kt (optional)
│       └── [Module]Importer.kt (optional)
├── infrastructure/
│   ├── persistence/
│   │   ├── Ktorm[Module]Repository.kt
│   │   └── Ktorm[Module]sCriteriaParser.kt
│   ├── bus/
│   │   ├── Ktorm[Module]CommandHandlers.kt
│   │   └── Ktorm[Module]QueryHandlers.kt
│   └── services/
│       └── [Type]ImportReader.kt (e.g., CsvImportReader)
```

## Naming Conventions

### Entities and Value Classes

- Main entity: `FederalEntity`, `Municipality`, `Agency`
- ID: `FederalEntityId`, `MunicipalityId` (data class with UUID)
- Typed fields: `FederalEntityKeyCode`, `FederalEntityName`
- Dates: `FederalEntityCreateDate`, `FederalEntityUpdateDate`
- Field enums: `FederalEntityFields` (for Criteria)

### Errors

File: `[Module]Errors.kt`

```kotlin
sealed class [Module]Error(override val message: String, override val cause: Throwable? = null) : Error()

class [Module]NotFoundError(val id: String) : [Module]Error("The [module] with id <$id> was not found")

class [Module]AlreadyExistsError(val key: String) : [Module]Error("The [module] with key <$key> already exists")

class CanNotSave[Module]Error(cause: Throwable?) : [Module]Error("The [module] could not be saved", cause)

class CanNotDelete[Module]Error(cause: Throwable?) : [Module]Error("The [module] could not be deleted", cause)
```

### Commands

- `Create[Module]Command(keyCode: String, name: String)`
- `Update[Module]Command(id: String, keyCode: String, name: String)`
- `Delete[Module]Command(id: String)`
- `CsvImport[Module]sCommand(path: String)` (optional)

Extend `Command<[Module]Error, ReturnType>` where ReturnType is `UUID` for create/update or `Unit` for delete.

### Queries

- `Search[Module]sQuery(search: String?, limit: Int?, offset: Int?)`
- `Search[Module]ByIdQuery(id: String)`
- `Search[Module]sByTermQuery(term: String, limit: Int?)`

### Use Cases (Application Services)

- **Creator**: `class [Module]Creator(private val repository: [Module]Repository)`
- **Updater**: `class [Module]Updater(private val repository: [Module]Repository)`
- **Deleter**: `class [Module]Deleter(private val repository: [Module]Repository)`
- **Searcher**: `class [Module]Searcher(private val repository: [Module]Repository)`
- **Importer**: `class [Module]Importer<T>(private val reader: [Module]ImportReader<T>, private val repository: [Module]Repository)`

### Responses (DTOs)

- `data class [Module]Response(id: String, keyCode: String, name: String, createdAt: Date, updatedAt: Date?)`
- `data class [Module]sResponse(items: List<[Module]Response>)`
- `data class Simple[Module]Response(id: String, name: String)` (for concise responses)

## Implementation Patterns

### 1. Entity with Value Classes

```kotlin
class [Module] private constructor(
    private val id: [Module]Id,
    private var keyCode: [Module]KeyCode,
    private var name: [Module]Name,
    private val createdAt: [Module]CreateDate,
    private var updatedAt: [Module]UpdateDate?
) {
    companion object {
        fun create(keyCode: String, name: String) = [Module](
            [Module]Id.random(),
            [Module]KeyCode(keyCode),
            [Module]Name(name),
            [Module]CreateDate.now(),
            null
        )

        fun from(id: String, keyCode: String, name: String, createdAt: Date, updatedAt: Date?) = [Module](
            [Module]Id.fromString(id),
            [Module]KeyCode(keyCode),
            [Module]Name(name),
            [Module]CreateDate(createdAt),
            updatedAt?.let { [Module]UpdateDate(it) }
        )
    }

    fun id() = id.value
    fun keyCode() = keyCode.value
    fun name() = name.value
    fun createdAt() = createdAt.value
    fun updatedAt() = updatedAt?.value

    fun changeName(name: String) {
        this.name = [Module]Name(name)
        this.updatedAt = [Module]UpdateDate.now()
    }
}

data class [Module]Id(val value: UUID) {
    companion object {
        fun random() = [Module]Id(UUID.randomUUID())
        fun fromString(value: String) = try {
            [Module]Id(UUID.fromString(value))
        } catch (e: Exception) {
            throw Invalid[Module]IdError(value, e)
        }
    }
}

data class [Module]KeyCode(val value: String) {
    init { validate() }
    private fun validate() {
        if (value.isBlank()) throw Invalid[Module]KeyCodeError(value)
    }
}
```

### 2. Repository Interface

```kotlin
interface [Module]Repository {
    fun matching(criteria: Criteria): List<[Module]>
    fun count(criteria: Criteria): Int
    fun save([module]: [Module])
    fun delete([module]Id: String)
}
```

### 3. Command Handler

```kotlin
class Create[Module]CommandHandler(private val creator: [Module]Creator) :
    CommandHandler<[Module]Error, UUID, Create[Module]Command> {
    override fun handle(command: Create[Module]Command): Either<[Module]Error, UUID> {
        return creator.create(command.keyCode(), command.name())
    }
}
```

### 4. Use Case Creator

```kotlin
class [Module]Creator(private val repository: [Module]Repository) {
    fun create(keyCode: String, name: String): Either<[Module]Error, UUID> {
        return try {
            repository.matching([Module]Criteria.keyCodeCriteria(keyCode))
                .firstOrNull()
                ?.let { throw [Module]AlreadyExistsError(keyCode) }

            val [module] = [Module].create(keyCode, name)
            repository.save([module])
            Right([module].id())
        } catch (e: [Module]Error) {
            Left(e)
        } catch (e: Exception) {
            Left(CanNotSave[Module]Error(e))
        }
    }
}
```

### 5. Criteria

```kotlin
enum class [Module]Fields(val value: String) {
    Id("id"),
    KeyCode("keyCode"),
    Name("name"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}

object [Module]Criteria {
    fun idCriteria(id: String) = Criteria(
        SingleFilter.equal([Module]Fields.Id.value, id),
        Orders.none(),
        1,
        null
    )

    fun keyCodeCriteria(keyCode: String) = Criteria(
        SingleFilter.equal([Module]Fields.KeyCode.value, keyCode),
        Orders.none(),
        1,
        null
    )

    fun anotherKeyCodeCriteria(id: String, keyCode: String) = Criteria(
        AndFilters(
            SingleFilter.notEqual([Module]Fields.Id.value, id),
            SingleFilter.equal([Module]Fields.KeyCode.value, keyCode)
        ),
        Orders.none(),
        1,
        null
    )

    fun searchCriteria(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = Criteria(
        search?.let {
            OrFilters(
                SingleFilter.contains([Module]Fields.KeyCode.value, it),
                SingleFilter.contains([Module]Fields.Name.value, it)
            )
        } ?: EmptyFilters(),
        orders?.let {
            val fields = [Module]Fields.entries.map { it.value }
            val filteredOrders = orders.mapNotNull { it.takeIf { fields.contains(it["orderBy"]) } }
            Orders.fromValues(filteredOrders.toTypedArray())
        } ?: Orders(Order.asc([Module]Fields.Name.value)),
        limit,
        offset
    )
}
```

### 6. Ktorm Repository Implementation

```kotlin
class Ktorm[Module]Repository(private val database: Database) : [Module]Repository {
    private val parser = Ktorm[Module]sCriteriaParser()

    override fun matching(criteria: Criteria): List<[Module]> {
        return database.from([Table])
            .select()
            .where { parser.parse(criteria.filters) }
            .limit(criteria.limit ?: Int.MAX_VALUE, criteria.offset)
            .orderBy(parser.parse(criteria.orders))
            .map { row -> row.to[Module]() }
    }

    override fun count(criteria: Criteria): Int {
        return database.from([Table])
            .select()
            .where { parser.parse(criteria.filters) }
            .count()
    }

    override fun save([module]: [Module]) {
        database.insert([Table]) {
            set("id", UUID.fromString([module].id()))
            set("key_code", [module].keyCode())
            set("name", [module].name())
            set("created_at", [module].createdAt())
            [module].updatedAt()?.let { set("updated_at", it) }
        }
    }

    override fun delete([module]Id: String) {
        database.delete([Table]) { it["id"] eq UUID.fromString([module]Id) }
    }

    private fun ResultRow.to[Module](): [Module] = [Module].from(
        this[Table.id].toString(),
        this[Table.keyCode],
        this[Table.name],
        this[Table.createdAt],
        this[Table.updatedAt]
    )
}
```

### 7. Command Handlers Registration

```kotlin
class Ktorm[Module]CommandHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>> = mapOf(
        Create[Module]Command::class to createCommandHandler(),
        Update[Module]Command::class to updateCommandHandler(),
        Delete[Module]Command::class to deleteCommandHandler()
    )

    private fun createCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val creator: [Module]Creator by inject()
        val commandHandler = Create[Module]CommandHandler(creator)
        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun updateCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val updater: [Module]Updater by inject()
        val commandHandler = Update[Module]CommandHandler(updater)
        return KtormCommandHandlerDecorator(database, commandHandler)
    }

    private fun deleteCommandHandler(): CommandHandler<*, *, out Command<*, *>> {
        val deleter: [Module]Deleter by inject()
        val commandHandler = Delete[Module]CommandHandler(deleter)
        return KtormCommandHandlerDecorator(database, commandHandler)
    }
}
```

### 8. Dependency Registration in KtormAppModule

```kotlin
// Repositories
single<[Module]Repository> { Ktorm[Module]Repository(get()) }

// [Module] services
single { [Module]Creator(get()) }
single { [Module]Deleter(get(), get()) }
single { [Module]Searcher(get()) }
single { [Module]Updater(get(), get()) }
```

## Test Structure

### In-Memory Repository for Tests

```
core/src/test/kotlin/org/eduardoleolim/organizadorpec660/[module]/infrastructure/persistence/
├── InMemory[Module]Repository.kt
└── InMemory[Module]sCriteriaParser.kt
```

```kotlin
class InMemory[Module]Repository : [Module]Repository {
    val records = mutableMapOf<String, [Module]>()

    override fun matching(criteria: Criteria): List<[Module]> {
        return records.values.toList().filter { true } // Implement filters
    }

    override fun count(criteria: Criteria): Int = records.size

    override fun save([module]: [Module]) {
        records[[module].id().toString()] = [module]
    }

    override fun delete([module]Id: String) {
        records.remove([module]Id)
    }
}
```

### Use Case Test

```kotlin
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class [Module]CreatorTest {
    private val repository = InMemory[Module]Repository()
    private val creator = [Module]Creator(repository)

    @BeforeEach
    fun beforeEach() {
        repository.records.clear()
    }

    @Test
    fun `create a [module]`() {
        val keyCode = "30"
        val name = "NAME"

        creator.create(keyCode, name).fold(
            ifRight = { assert(repository.records.size == 1) },
            ifLeft = { assert(false) }
        )
    }

    @Nested
    inner class ValidationTests {
        @BeforeEach
        fun beforeEach() {
            [Module].create("30", "NAME").let {
                repository.records[it.id().toString()] = it
            }
        }

        @Test
        fun `fail if [module] already exists`() {
            creator.create("30", "NAME").fold(
                ifRight = { assert(false) },
                ifLeft = { assert(it is [Module]AlreadyExistsError) }
            )
        }
    }
}
```

## General Rules

1. **Arrow Either**: All use cases return `Either<[Module]Error, ReturnType>`
2. **Trim and Uppercase**: Commands normalize inputs with `.trim().uppercase()`
3. **Value Classes**: All entity fields use value classes with validation in init
4. **Typed Errors**: Each module has its own sealed class of errors
5. **Criteria pattern**: Use `SingleFilter`, `AndFilters`, `OrFilters`, `EmptyFilters`
6. **Koin**: All dependencies are registered in `KtormAppModule`
7. **Decorators**: Command/Query handlers use Ktorm decorators for transactions
8. **Test repos**: In-memory implementations in src/test for unit testing